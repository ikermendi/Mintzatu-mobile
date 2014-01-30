//
//  BabesleakViewController.m
//  Mintzatu
//
//  Created by Sergio Garcia on 30/01/14.
//  Copyright (c) 2014 Irontec S.L. All rights reserved.
//

#import "BabesleakViewController.h"

@interface BabesleakViewController () <UIWebViewDelegate>

@end

@implementation BabesleakViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    NSString *_fileName = @"babesleak";
    NSURL *url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:_fileName ofType:@"html"]isDirectory:NO];
    [_webView loadRequest:[NSURLRequest requestWithURL:url]];
    _webView.delegate = self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType;
{
    NSURL *requestURL = [request URL];
    if (([[requestURL scheme] isEqualToString: @"http" ] || [[requestURL scheme] isEqualToString: @"https"] || [[requestURL scheme] isEqualToString:@"mailto"])
        && (navigationType == UIWebViewNavigationTypeLinkClicked)) {
        return ![[UIApplication sharedApplication] openURL:requestURL];
    }
    return YES;
}

@end
