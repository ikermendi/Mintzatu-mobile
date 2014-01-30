//
//  LoginViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 20/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LoginViewController.h"

#import "LoginView.h"
#import "IntroView.h"
#import "LoadingBackground.h"
#import "MintzatuAPIClient.h"
#import "AppDelegate.h"
#import "ZAActivityBar.h"
#import "MBProgressHUD.h"
#import <FacebookSDK/FacebookSDK.h>


#define MAX_LOGIN_ATTEMPT 2

static NSString *kfrontKey = @"frontKey";
static NSString *kbackKey = @"backKey";

@interface LoginViewController() <UIScrollViewDelegate, LoginDelegate, UIAlertViewDelegate, FBLoginViewDelegate>
{
    NSTimer *_timer;
    NSUInteger _loginAttempt;
}
@property (nonatomic, strong) NSArray *contentList;
@property (nonatomic, strong) LoginView *loginView;
@property (nonatomic, strong) LoadingBackground *loadingBackground;
@property (nonatomic, strong) NSMutableArray *images;
@property (nonatomic, strong) UIKeyboardListener *keyboardListener;
@property (weak, nonatomic) IBOutlet JSFlatButton *loginButton;
@property (weak, nonatomic) IBOutlet JSFlatButton *registerButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *parteHartuButton;
@property (nonatomic, weak) IBOutlet UIScrollView *scrollView;
@property (nonatomic, weak) IBOutlet UIPageControl *pageControl;
@property (weak, nonatomic) IBOutlet FBLoginView *fbLoginView;

- (IBAction)login:(UIButton *)sender;

@end

@implementation LoginViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        NSString *path = [[NSBundle mainBundle] pathForResource:@"SplashImages" ofType:@"plist"];
        _contentList = [NSArray arrayWithContentsOfFile:path];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showKeyboard)
                                                     name:UITextFieldTextDidBeginEditingNotification object:nil];
        
        _keyboardListener = [UIKeyboardListener shared];
        _loginAttempt = 0;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.screenName = @"LoginViewController";
    
    self.fbLoginView.readPermissions = @[@"basic_info",
                                        @"email"];
    
    [self.loginButton setButtonBackgroundColor:[UIColor mintzatuBlue]];
    [self.loginButton setButtonForegroundColor:[UIColor mintzatuBlue]];
    [self.loginButton setTitle:@"Sartu" forState:UIControlStateNormal];
    [self.loginButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.loginButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
    
    [self.registerButton setButtonBackgroundColor:[UIColor mintzatuBlue]];
    [self.registerButton setButtonForegroundColor:[UIColor mintzatuBlue]];
    [self.registerButton setTitle:@"Erregistratu" forState:UIControlStateNormal];
    [self.registerButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.registerButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
    
    NSUInteger numberPages = self.contentList.count;
    
    // view controllers are created lazily
    // in the meantime, load the array with placeholders which will be replaced on demand
    NSMutableArray *controllers = [[NSMutableArray alloc] init];
    for (NSUInteger i = 0; i < numberPages; i++)
    {
		[controllers addObject:[NSNull null]];
    }
    
    _images = controllers;
    
    // a page is the width of the scroll view
    _scrollView.pagingEnabled = YES;
    _scrollView.contentSize =
    CGSizeMake(CGRectGetWidth(self.scrollView.frame) * numberPages, CGRectGetHeight(self.scrollView.frame));
    _scrollView.showsHorizontalScrollIndicator = NO;
    _scrollView.showsVerticalScrollIndicator = NO;
    _scrollView.scrollsToTop = NO;
    _scrollView.delegate = self;
    
    _pageControl.numberOfPages = numberPages;
    _pageControl.currentPage = 0;
    
    
 
    //Pages are no created at demand
    for (int i = 0; i < _images.count; i++) {
        [self loadScrollViewWithPage:i];
    }
    
    _timer = [NSTimer scheduledTimerWithTimeInterval:3.0f target:self selector:@selector(timer) userInfo:nil repeats:YES];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    _scrollView.contentSize = CGSizeMake(_scrollView.contentSize.width,_scrollView.frame.size.height);
    
    //Set Facebook login button in view
    CGRect frame = _fbLoginView.frame;
    frame.origin.y = _loginButton.frame.origin.y + _loginButton.frame.size.height + 10;
    [_fbLoginView setFrame:frame];
    

}

- (void)timer
{
    self.pageControl.currentPage++;
    if (self.pageControl.currentPage == _images.count) {
        [_timer invalidate];
    }
    [self changePage:nil];
}


#pragma mark IBAction methods

- (IBAction)login:(UIButton *)sender
{
    if (_loginView == nil) {
        _loginView = [[LoginView alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/2 - 125, self.view.bounds.size.height + 125, 250, 150) delegate:self];
    } else {
        _loginView.usernameField.text = @"";
        _loginView.passwordField.text = @"";
    }
    
    if (_loadingBackground == nil) {
        _loadingBackground = [[LoadingBackground alloc] initWithFrame:self.view.bounds];
        [_loadingBackground becomeFirstResponder];
    }
    
    [self.view addSubview:_loadingBackground];
    [self.view addSubview:_loginView];
    
    [UIView animateWithDuration:.75f animations:^{
        CGRect newFrame = _loginView.frame;
        newFrame.origin = CGPointMake(self.view.bounds.size.width/2 - 125, self.view.bounds.size.height/2 - 170);
        _loadingBackground.alpha = 1.0f;
        _loginView.frame = newFrame;
    } completion:^(BOOL finished) {
        [_loginView.usernameField becomeFirstResponder];
    }];
}

- (IBAction)changePage:(id)sender
{
    [self gotoPage:YES];    // YES = animate
}

- (IBAction)showLoginPage:(id)sender
{
    [_timer invalidate];
    
    self.pageControl.currentPage = 0;
    [self gotoPage:YES];
    
    [UIView animateWithDuration:1.0f animations:^{
        _loginButton.alpha = 1.0f;
        _registerButton.alpha = 1.0f;
        _fbLoginView.alpha = 1.0f;
        _pageControl.hidden = YES;
        [self toggleBarButton:NO];
    } completion:nil];
}

#pragma mark LoginView control

-(void)showKeyboard
{
    if(_keyboardListener.visible == YES)
        return;
    
    if (_loginView.usernameField.isEditing == YES) {
        [_loginView.usernameField resignFirstResponder];
    } else {
        [_loginView.passwordField resignFirstResponder];
    }
    
    [UIView animateWithDuration:.75f animations:^{
        CGPoint center = _loginView.center;
        _loginView.center = center;
    } completion:^(BOOL finished) {
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidBeginEditingNotification object:nil];
        [_loginView.usernameField becomeFirstResponder];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showKeyboard)
                                                     name:UITextFieldTextDidBeginEditingNotification object:nil];
    }];
}


- (void)hideLoginView
{
    [UIView animateWithDuration:.75f animations:^{
        _loadingBackground.alpha = 0.0f;
        _loginView.frame = CGRectMake(self.view.bounds.size.width/2 - 125, self.view.bounds.size.height + 125, 250, 150);
    } completion:^(BOOL finished) {
        [_loadingBackground removeFromSuperview];
        [_loginView removeFromSuperview];
    }];
}

-(void)toggleBarButton:(bool)show
{
    if (show) {
        _parteHartuButton.style = UIBarButtonItemStyleBordered;
        _parteHartuButton.enabled = true;
        _parteHartuButton.title = @"Parte hartu";
    } else {
        _parteHartuButton.style = UIBarButtonItemStylePlain;
        _parteHartuButton.enabled = false;
        if (SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
            _parteHartuButton.title = nil;
        }
    }
}


#pragma mark Facebook loginview delegate

- (void)loginViewShowingLoggedInUser:(FBLoginView *)loginView
{
    [FBRequestConnection
     startForMeWithCompletionHandler:^(FBRequestConnection *connection,
                                       id<FBGraphUser> user,
                                       NSError *error) {
         if (!error) {
             NSString *userInfo = @"";

             userInfo = [userInfo
                         stringByAppendingString:
                         [NSString stringWithFormat:@"Name: %@\n\n",
                          user.name]];
                          
             MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
             
             NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
             [params setValue:user.name forKey:@"username"];
             [params setValue:[user objectForKey:@"email"] forKey:@"email"];
             [params setValue:user.first_name forKey:@"firstName"];
             [params setValue:user.last_name forKey:@"lastName"];
             //[params setValue:user.id forKey:@"idFb"];
             [params setValue:[user objectForKey:@"id"] forKey:@"idFb"];
             [params setValue:@"true" forKey:@"fb"];
             NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
             [params setValue:[defaults stringForKey:@"APN_UID"] forKey:@"uuid"];
             
             [[MintzatuAPIClient sharedClient] postPath:@"register" parameters:params success:^(AFHTTPRequestOperation *operation, id json) {
                 [self loginSuccessWithJSON:json];
                 [hud hide:YES];
             } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                 [hud hide:YES];
             }];
         }
     }];
    
    for (id obj in self.fbLoginView.subviews) {
        if ([obj isKindOfClass:[UILabel class]]) {
            UILabel * loginLabel =  obj;
            loginLabel.text = @"Saioa itxi";
            //loginLabel.textAlignment = NSTextAlignmentCenter;
            loginLabel.frame = CGRectMake(0, 2, 230, 40);
        }
    }
}

- (void)loginViewShowingLoggedOutUser:(FBLoginView *)loginView
{
    for (id obj in self.fbLoginView.subviews) {
        if ([obj isKindOfClass:[UILabel class]]) {
            UILabel * loginLabel =  obj;
            loginLabel.text = @"Facebook-ekin sartu";
            //loginLabel.textAlignment = NSTextAlignmentCenter;
            loginLabel.frame = CGRectMake(0, 2, 230, 40);
        }
    }
}

#pragma mark Page control


- (void)loadScrollViewWithPage:(NSUInteger)page
{
    if (page >= self.contentList.count)
        return;
    
    IntroView *v = (IntroView*) [_images objectAtIndex:page];
    if ((NSNull *)v == [NSNull null]) {
        NSDictionary *numberItem = [self.contentList objectAtIndex:page];
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"IntroView" owner:nil options:nil];
        v = [nib objectAtIndex:0];
        v.frontImage.image = [UIImage imageNamed:[numberItem valueForKey:kfrontKey]];
        v.backgroundImage.image = [UIImage imageNamed:[numberItem valueForKey:kbackKey]];
        [_images replaceObjectAtIndex:page withObject:v];
    }
    
    CGRect frame = self.scrollView.frame;
    frame.origin.x = CGRectGetWidth(frame) * page;
    frame.origin.y = 0;
    v.frame = frame;
    
    [self.scrollView addSubview:v];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [_timer invalidate];
    
    [UIView animateWithDuration:0.5f animations:^{
        _registerButton.alpha = 0.0f;
        _loginButton.alpha = 0.0f;
        _fbLoginView.alpha = 0.0f;
        _pageControl.hidden = NO;
        [self toggleBarButton:YES];
    }];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    // switch the indicator when more than 50% of the previous/next page is visible
    CGFloat pageWidth = CGRectGetWidth(self.scrollView.frame);
    NSUInteger page = floor((self.scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    self.pageControl.currentPage = page;
    
    // load the visible page and the page on either side of it (to avoid flashes when the user starts scrolling)
    //Pages are not created at demand
    /*[self loadScrollViewWithPage:page - 1];
    [self loadScrollViewWithPage:page];
    [self loadScrollViewWithPage:page + 1];*/
    
    // a possible optimization would be to unload the views+controllers which are no longer visible
}

- (void)gotoPage:(BOOL)animated
{
    NSInteger page = self.pageControl.currentPage;
    
    // load the visible page and the page on either side of it (to avoid flashes when the user starts scrolling)
    /*[self loadScrollViewWithPage:page - 1];
    [self loadScrollViewWithPage:page];
    [self loadScrollViewWithPage:page + 1];*/
    
	// update the scroll view to the appropriate page
    CGRect bounds = self.scrollView.bounds;
    bounds.origin.x = CGRectGetWidth(bounds) * page;
    bounds.origin.y = 0;
    [self.scrollView scrollRectToVisible:bounds animated:animated];
}

#pragma mark Login delegate

- (void)loginWithUsername:(NSString*)username password:(NSString*)password
{
    if (username.length == 0 || password.length == 0) {
        [ZAActivityBar showErrorWithStatus:@"Posta eta pasahitza beharrezkoak dira" duration:2.0f];
        return;
    }
    
    if (![MintzatuAPIClient isValidEmail:username]) {
        [ZAActivityBar showErrorWithStatus:@"Posta ez da zuzena" duration:2.0f];
        return;
    }
    
    _loginAttempt++;
    
    [_loginView.usernameField resignFirstResponder];
    [_loginView.passwordField resignFirstResponder];
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.loginView animated:YES];
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:username forKey:@"user"];
    [params setValue:password forKey:@"password"];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [params setValue:[defaults stringForKey:@"APN_UID"] forKey:@"uuid"];
        
    [[MintzatuAPIClient sharedClient] postPath:@"login" parameters:params success:^(AFHTTPRequestOperation *operation, id json) {
        [self loginSuccessWithJSON:json];
        [hud hide:YES];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        if (_loginAttempt == MAX_LOGIN_ATTEMPT) {
            UIAlertView  *alert = [[UIAlertView alloc] initWithTitle:@"Abisua" message:@"Pasahitza berreskuratu nahi duzu?" delegate:self cancelButtonTitle:@"Ezeztatu" otherButtonTitles:@"Berreskuratu", nil];
            [alert show];
            _loginAttempt = 0;
        }
        [hud hide:YES];
    }];
}

- (void)loginSuccessWithJSON:(id)json
{
    [MintzatuAPIClient saveUserId:[json objectForKey:@"id"]];
    [MintzatuAPIClient saveUserToken:[json objectForKey:@"token"]];
    [MintzatuAPIClient saveUserFullname:[json objectForKey:@"fullname"]];
    
    [self hideLoginView];
    
    AppDelegate *delegate = [[UIApplication sharedApplication] delegate];
    [delegate loadMainController];
}

- (void)loginDismiss
{
    if (_loginView.usernameField.isEditing == YES) {
        [_loginView.usernameField resignFirstResponder];
    } else if (_loginView.passwordField.isEditing == YES) {
        [_loginView.passwordField resignFirstResponder];
    } else {
        [_loginView.usernameField resignFirstResponder];
        [_loginView.passwordField resignFirstResponder];
    }
    
    double delayInSeconds = 0.5;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self hideLoginView];
    });
}

#pragma mark AlertView

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != alertView.cancelButtonIndex) {
        [_loginView.usernameField resignFirstResponder];
        [_loginView.passwordField resignFirstResponder];
        [self hideLoginView];
        [self performSegueWithIdentifier:@"forgot" sender:nil];
    }
}

#pragma mark Other stuff

- (void) dealloc {
    
    self.contentList = nil;
    self.loginView = nil;
    self.loadingBackground = nil;
    self.images = nil;
    self.keyboardListener = nil;
    self.scrollView = nil;
    self.pageControl = nil;
    self.loginButton = nil;
    self.registerButton = nil;
    self.parteHartuButton = nil;
    _timer = nil;
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidBeginEditingNotification object:nil];
}

@end
