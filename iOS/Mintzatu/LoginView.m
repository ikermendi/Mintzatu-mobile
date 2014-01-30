//
//  LoginView.m
//  MintzatuLogin
//
//  Created by Iker Mendilibar Fernandez on 16/04/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LoginView.h"

@implementation LoginView

- (id)initWithFrame:(CGRect)frame delegate:(id<LoginDelegate>)delegate
{
    self = [super initWithFrame:frame];
    if (self) {
        
        self.delegate = delegate;
        
        self.backgroundColor = [UIColor backgroundBeige];
        self.layer.cornerRadius = 5.0;
        self.layer.borderColor = [UIColor lightGrayColor].CGColor;
        self.layer.shadowColor = [UIColor blackColor].CGColor;
        self.layer.shadowOpacity = 0.8;
        self.layer.shadowRadius = 3.0;
        self.layer.shadowOffset = CGSizeMake(2.0, 2.0);
        
        _usernameField = [[UITextField alloc] initWithFrame:CGRectMake(frame.size.width/2 - 110, 10, 220, 35)];
        _usernameField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        _usernameField.backgroundColor = [UIColor whiteColor];
        _usernameField.layer.sublayerTransform = CATransform3DMakeTranslation(5, 0, 0);
        _usernameField.placeholder = @"Posta";
        _usernameField.layer.borderColor = [[UIColor lightGrayColor] CGColor];
        _usernameField.layer.borderWidth = 1.0f;
        _usernameField.autocorrectionType = UITextAutocorrectionTypeNo;
        _usernameField.autocapitalizationType = UITextAutocapitalizationTypeNone;
        
        _passwordField = [[UITextField alloc] initWithFrame:CGRectMake(frame.size.width/2 - 110, 55, 220, 35)];
        _passwordField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        _passwordField.backgroundColor = [UIColor whiteColor];
        _passwordField.layer.sublayerTransform = CATransform3DMakeTranslation(5, 0, 0);
        _passwordField.placeholder = @"Pasahitza";
        _passwordField.layer.borderColor = [[UIColor lightGrayColor] CGColor];
        _passwordField.layer.borderWidth = 1.0f;
        _passwordField.secureTextEntry = YES;
        _passwordField.autocorrectionType = UITextAutocorrectionTypeNo;
        
        _cancelButton = [[JSFlatButton alloc] initWithFrame:CGRectMake(20, 98, 100, 45) backgroundColor:[UIColor buttonBeige] foregroundColor:[UIColor buttonBeige]];
        _cancelButton.titleLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size: 15.0];
        [_cancelButton setTitle:@"Ezeztatu" forState:UIControlStateNormal];
        [_cancelButton setTitleColor:[UIColor darkGrayColor] forState:UIControlStateHighlighted];
        [_cancelButton setTitleColor:[UIColor darkGrayColor] forState:UIControlStateNormal];
        [_cancelButton addTarget:self action:@selector(cancelClicked) forControlEvents:UIControlEventTouchUpInside];
        
        _loginButton = [[JSFlatButton alloc] initWithFrame:CGRectMake(130, 98, 100, 45) backgroundColor:[UIColor mintzatuBlue] foregroundColor:[UIColor mintzatuBlue]];
        _loginButton.titleLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size: 15.0];
        [_loginButton setTitle:@"Sartu" forState:UIControlStateNormal];
        [_loginButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_loginButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
        [_loginButton addTarget:self action:@selector(loginClicked) forControlEvents:UIControlEventTouchUpInside];
        
        [self addSubview:_usernameField];
        [self addSubview:_passwordField];
        [self addSubview:_cancelButton];
        [self addSubview:_loginButton];
    }
    return self;
}

- (void)loginClicked
{
    [self.delegate loginWithUsername:_usernameField.text password:_passwordField.text];
}

- (void)cancelClicked
{
    [self.delegate loginDismiss];
}

@end