//
//  ForgotPasswordViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 16/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "ForgotPasswordViewController.h"

#import "MintzatuAPIClient.h"
#import "MBProgressHUD.h"
#import "JSFlatButton.h"
#import "ZAActivityBar.h"

@interface ForgotPasswordViewController () <UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *postaTextField;
@property (weak, nonatomic) IBOutlet JSFlatButton *sendButton;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@end

@implementation ForgotPasswordViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"ForgotPasswordViewController";
    _postaTextField.delegate = self;
    
    _containerView.layer.borderWidth = 1.0f;
    _containerView.layer.borderColor = [UIColor backgroundBeige].CGColor;
    
    [self.sendButton setButtonBackgroundColor:[UIColor mintzatuBlue]];
    [self.sendButton setButtonForegroundColor:[UIColor mintzatuBlue]];
    [self.sendButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.sendButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
}

- (IBAction)postPasswordReminderRequest:(id)sender
{
    [_postaTextField resignFirstResponder];
    
    NSString *email = _postaTextField.text;
    
    if (![MintzatuAPIClient isValidEmail:email]) {
        [ZAActivityBar showErrorWithStatus:@"Posta ez da zuzena"];
        return;
    }
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:email forKey:@"email"];
    
    [[MintzatuAPIClient sharedClient] postPath:@"remind-password" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [hud hide:YES];
        [self.navigationController popViewControllerAnimated:YES];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [hud hide:YES];
    }];
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    [self postPasswordReminderRequest:nil];
    return NO;
}

@end
