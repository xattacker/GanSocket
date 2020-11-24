//
//  HomeMenuViewController.swift
//  GanClientApp
//
//  Created by xattacker on 2015/6/22.
//  Copyright (c) 2015年 xattacker. All rights reserved.
//

import UIKit
import RxSwift
import RxCocoa


class ViewController: BaseViewController
{
    private let disposeBag = DisposeBag()
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
    }

    override func viewDidAppear(_ animated: Bool)
    {
        super.viewDidAppear(animated)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
    }

    @IBAction func onRefreshAction(_ sender: AnyObject)
    {
    }
}
