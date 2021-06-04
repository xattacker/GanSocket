package com.xattacker.android.main

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast

import com.xattacker.android.main.databinding.ActivityMainBinding
import com.xattacker.gan.GanClient
import com.xattacker.gan.GanClientListener
import com.xattacker.gan.data.MessageData
import com.xattacker.util.DateTimeFormatType
import com.xattacker.util.DateTimeUtility

import java.util.*

class MainActivity() : Activity(), GanClientListener
{
    companion object
    {
        private val ACCOUNT = "test2"
        private val PASSWORD = "test2"
    }

    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // use view Binding mode
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        this.title = "GanClientApp"

        this.loadIP("GanClient", {
            ip: String, port: Int ->
            binding.editIp.setText(ip)
            binding.editPort.setText(port.toString())
        })

        binding.editReceiver.setText(PASSWORD)
    }

    public override fun onDestroy()
    {
        super.onDestroy()

        GanClient.release()
    }

    public fun onLoginClick(view: View)
    {
        val account = ACCOUNT
        val pwd = PASSWORD

        asyncRun(
            {
                val result: Boolean = GanClient.instance?.accountService?.login(account, pwd) ?: false
                Log.i("aaa", "login $result")
                showToast("login result: $result")
            })
    }

    public fun onLogoutClick(view: View)
    {
        asyncRun(
            {
                val result: Boolean = GanClient.instance?.accountService?.logout() ?: false
                Log.i("aaa", "logout $result")
                showToast("logout result: $result")
            })

        binding.textGotMessage.setText("")
    }

    public fun onGetIPClick(view: View)
    {
        asyncRun(
            {
                val ip = GanClient.instance?.systemService?.getIP()
                if (ip != null)
                {
                    Log.i("aaa", "get IP: $ip")
                    showToast("get IP:  $ip")
                }
                else
                {
                    showToast("get IP failed ")
                }
            })
    }

    public fun onGetTimeClick(view: View)
    {
        asyncRun(
            {
                val time = GanClient.instance?.systemService?.getSystemTime()
                if (time != null)
                {
                    val time_str: String = DateTimeUtility.getDateTimeString(time, DateTimeFormatType.DATETIME_COMPLETE)
                    Log.i("aaa", "get time: $time_str")
                    showToast("get time:  $time_str")
                }
                else
                {
                    showToast("get time failed")
                }
            })
    }

    public fun onSendMsgClick(view: View)
    {
        val receiver = binding.editReceiver.text.toString()
        val msg = "send test message=" + System.currentTimeMillis()

        asyncRun(
            {
                val result: Boolean = GanClient.instance?.messageService?.sendMessage(receiver, msg) ?: false
                Log.i("aaa", "send msg $result")
                showToast("send msg result: $result")
            })
    }

    override fun onAccountLoggedIn(account: String)
    {
        Log.i("aaa", "onAccountLoggedIn $account")

        this.title = account + "(loggedIn)"
    }

    override fun onAccountLoggedOut(account: String)
    {
        Log.i("aaa", "onAccountLoggedOut $account")

        this.title = "GanClientApp"
    }

    override fun onMessageReceived(message: MessageData)
    {
        Log.i("aaa", "onMessageReceived $message")

        runOnUiThread(Runnable {

            binding.textGotMessage.setText("")

            val date = Date(message.time ?: 0)
            binding.textGotMessage.append(DateTimeUtility.getDateTimeString(date, DateTimeFormatType.DATETIME_COMPLETE))
            binding.textGotMessage.append("\n" + message.message)
        })
    }

    private fun asyncRun(run: () -> Unit, onError: ((th: Throwable) -> Unit)? = null, delay: Int = 0)
    {
        try
        {
            if (GanClient.instance == null)
            {
                try
                {
                    val ip = binding.editIp.text.toString()
                    val port = binding.editPort.text.toString().toInt()
                    GanClient.initial(ip, port, this)

                    saveIP("GanClient", ip, port)
                }
                catch (th: Throwable)
                {
                    showToast("initial GanClient failed: " + th.toString())
                    onError?.invoke(th)

                    return
                }
            }


            object : Thread()
            {
                override fun run()
                {
                    try
                    {
                        Looper.prepare()
                        if (delay > 0)
                        {
                            sleep(delay.toLong())
                        }
                        run()
                    }
                    catch (th: Throwable)
                    {
                        showToast(if (th.localizedMessage != null) th.localizedMessage else th.toString())
                        onError?.invoke(th)
                    }
                }
            }.start()
        }
        catch (th: Throwable)
        {
            showToast(th.localizedMessage)
            onError?.invoke(th)
        }
    }

    private fun showToast(text: String)
    {
        if (isMainThread)
        {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
        else
        {
            runOnUiThread(object : Runnable
            {
                override fun run()
                {
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private val isMainThread: Boolean
         get() = (Looper.getMainLooper() == Looper.myLooper())

    private val isOnEmulator: Boolean
        get()
        {
            val android_id: String = Settings.Secure.ANDROID_ID
            val product = Build.PRODUCT
            return ("sdk".equals(product, ignoreCase = true) ||
                    "google_sdk".equals(product, ignoreCase = true) ||
                    "sdk_x86".equals(product, ignoreCase = true) ||
                    "sdk_google_phone_x86".equals(product, ignoreCase = true) ||
                    "vbox86p".equals(product, ignoreCase = true) ||
                    (android_id == null) ||
                    android_id.equals("9774D56D682E549C", ignoreCase = true))
        }

    private fun loadIP(aName: String, loaded: (ip: String, port: Int) -> Unit)
    {
        val pref = getSharedPreferences(aName, MODE_PRIVATE)
        val ip = pref.getString("IP", null)
        val port = pref.getInt("PORT", 0)
        if (ip != null && port != 0)
        {
            loaded(ip, port)
        }
    }

    private fun saveIP(name: String, ip: String, port: Int)
    {
        val pref = getSharedPreferences(name, MODE_PRIVATE)
        pref.edit().putString("IP", ip).putInt("PORT", port).commit()
    }
}