//
//  LicenseViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 18/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LicenseViewController.h"

@interface LicenseViewController ()
{
    __weak IBOutlet UIWebView *_webView;
}
@end

@implementation LicenseViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"LicenseViewController";
    if (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_6_1) {
        //Odio iOS7
        self.edgesForExtendedLayout = UIRectEdgeNone;
    }
    
    NSURL *url = [NSURL URLWithString:@"http://mintzatu.com/orrialdeak/ikusi/orria/lizentziak_ios"];
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
	[_webView loadRequest:request];
}


@end
