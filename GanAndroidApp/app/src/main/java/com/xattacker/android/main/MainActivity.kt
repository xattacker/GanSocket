package com.xattacker.android.main

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.xattacker.gan.GanClient
import com.xattacker.gan.GanClientListener
import com.xattacker.gan.data.MessageData
import com.xattacker.util.DateTimeUtility
import com.xattacker.util.DateTimeUtility.DateTimeFormatType
import java.util.*

class MainActivity() : Activity(), View.OnClickListener, GanClientListener
{
    private var _accountEdit: EditText? = null
    private var _pwdEdit: EditText? = null

    private var _receiverEdit: EditText? = null
    private var _msgEdit: EditText? = null
    private var _receivedMsgEdit: EditText? = null

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        try
        {
            GanClient.initial("192.168.226.41", 5999, this)
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        setContentView(layout)
        
        _accountEdit = EditText(this)
        _accountEdit?.setText("test")
        layout.addView(_accountEdit)

        _pwdEdit = EditText(this)
        _pwdEdit?.setText("123")
        layout.addView(_pwdEdit)
        val layout2 = LinearLayout(this)
        layout2.orientation = LinearLayout.HORIZONTAL
        layout.addView(layout2)

        var button = Button(this)
        button.text = "login"
        button.id = 1
        button.setOnClickListener(this)
        layout2.addView(button)
        button = Button(this)
        button.text = "logout"
        button.id = 2
        button.setOnClickListener(this)
        layout2.addView(button)

        button = Button(this)
        button.text = "get ip"
        button.id = 4
        button.setOnClickListener(this)
        layout2.addView(button)

        button = Button(this)
        button.text = "get time"
        button.id = 5
        button.setOnClickListener(this)
        layout2.addView(button)

        _receiverEdit = EditText(this)
        _receiverEdit?.setText("test")
        layout.addView(_receiverEdit)
        _msgEdit = EditText(this)
        _msgEdit?.setText("1231234")
        layout.addView(_msgEdit)

        button = Button(this)
        button.text = "send sms"
        button.id = 3
        button.setOnClickListener(this)
        layout.addView(button)

        _receivedMsgEdit = EditText(this)
        _receivedMsgEdit?.gravity = Gravity.TOP
        _receivedMsgEdit?.setTextColor(Color.BLUE)
        _receivedMsgEdit?.isEnabled = false
        _receivedMsgEdit?.setLines(3)
        layout.addView(_receivedMsgEdit)
    }

    public override fun onDestroy()
    {
        super.onDestroy()

        GanClient.release()
    }

    override fun onClick(aView: View)
    {
        when (aView.id)
        {
            1 ->
            {
                val account = _accountEdit?.text.toString()
                val pwd = _pwdEdit?.text.toString()

                asyncRun(
                {
                        val result: Boolean = GanClient.instance?.accountService?.login(account, pwd) ?: false
                        Log.i("aaa", "login $result")
                        showToast("login result: $result")
                })
            }

            2 ->
            {
                asyncRun(
                {
                        val result: Boolean = GanClient.instance?.accountService?.logout() ?: false
                        Log.i("aaa", "logout $result")
                        showToast("logout result: $result")
                })

                _receivedMsgEdit?.setText("")
            }

            3 ->
            {
                val receiver = _receiverEdit?.text.toString()
                val msg = _msgEdit?.text.toString() + System.currentTimeMillis()

                asyncRun(
                {
                        val result: Boolean = GanClient.instance?.messageService?.sendMessage(receiver, msg) ?: false
                        Log.i("aaa", "send msg $result")
                        showToast("send msg result: $result")
                })
            }

            4 ->
            {
                asyncRun(
                {
                        val ip = GanClient.instance?.systemService?.getIPAddress()
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

            5 ->
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
        }
    }

    override fun onAccountLoggedIn(account: String)
    {
        Log.i("aaa", "onAccountLogined $account")
    }

    override fun onAccountLoggedOut(account: String)
    {
        Log.i("aaa", "onAccountClosed $account")
    }

    override fun onMessageReceived(message: MessageData)
    {
        Log.i("aaa", "onMessageReceived $message")

        runOnUiThread(Runnable {
            if (_receivedMsgEdit != null)
            {
                _receivedMsgEdit?.setText("")

                val date = Date(message.time ?: 0)
                _receivedMsgEdit?.append(DateTimeUtility.getDateTimeString(date, DateTimeFormatType.DATETIME_COMPLETE))
                _receivedMsgEdit?.append("\n" + message.message)
            }
        })
    }

    private fun asyncRun(run: () -> Unit, onError: ((th: Throwable) -> Unit)? = null, delay: Int = 0)
    {
        try
        {
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

    val appVersion: String?
        get()
        {
            var version: String? = null
            try
            {
                version = packageManager.getPackageInfo(packageName, 0).versionName
            }
            catch (ex: Exception)
            {
            }
            return version
        }

    private fun showToast(text: String?)
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
}