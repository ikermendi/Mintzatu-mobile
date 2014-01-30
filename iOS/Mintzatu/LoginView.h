//
//  LoginView.h
//  MintzatuLogin
//
//  Created by Iker Mendilibar Fernandez on 16/04/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

#import "UIKeyboardListener.h"
#import "JSFlatButton.h"

@protocol LoginDelegate <NSObject>
@required
- (void)loginWithUsername:(NSString*)username password:(NSString*)password;
- (void)loginDismiss;
@end

@interface LoginView : UIView
@property (nonatomic, strong) UITextField *usernameField;
@property (nonatomic, strong) UITextField *passwordField;
@property (nonatomic, strong) JSFlatButton *loginButton;
@property (nonatomic, strong) JSFlatButton *cancelButton;
@property (nonatomic, weak) UIKeyboardListener *keyboardListener;
@property (nonatomic, weak) id<LoginDelegate> delegate;
- (id)initWithFrame:(CGRect)frame delegate:(id<LoginDelegate>)delegate;
@end



