//
//  ErregistratuViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 22/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "ErregistratuViewController.h"

#import "DetailCell.h"
#import "MintzatuAPIClient.h"
#import "ZAActivityBar.h"
#import "MBProgressHUD.h"
#import "AppDelegate.h"
#import "SocialAccountWrapper.h"
#import "RegisterButtonCell.h"
#import "GAITrackedViewController.h"
#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

@interface ErregistratuViewController () <UITableViewDataSource, UITextFieldDelegate>
{
    BOOL _showingKeyboard;
    NSString *_fbUsername;
    NSString *_fbEmail;
    MBProgressHUD *_hud;
}
@end

@implementation ErregistratuViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    id tracker = [GAI sharedInstance].defaultTracker;
    [tracker set:kGAIScreenName value:@"ErregistratuViewController"];
    [tracker send:[[GAIDictionaryBuilder createAppView]  build]];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardDidHide) name:UIKeyboardDidHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardDidShow) name:UIKeyboardDidShowNotification object:nil];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardDidHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardDidShowNotification object:nil];
}


- (IBAction)registerUser:(id)sender
{
    if (_showingKeyboard)
        [self.view endEditing:YES];
    else
        [self keyboardDidHide];
}

-(void)keyboardDidShow
{
    _showingKeyboard = YES;
}

-(void)keyboardDidHide
{
    _showingKeyboard = NO;
    
    NSString *posta, *ezizena, *pasahitza, *pasahitza2;
    
    posta = ((DetailCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]]).detailTextField.text;
    ezizena = ((DetailCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0]]).detailTextField.text;
    pasahitza = ((DetailCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:2 inSection:0]]).detailTextField.text;
    pasahitza2 = ((DetailCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:3 inSection:0]]).detailTextField.text;
    
    if (posta.length == 0 || ezizena.length == 0 || pasahitza.length == 0 || pasahitza2.length == 0) {
        [ZAActivityBar showErrorWithStatus:@"Datu guztiak beharrezkoak dira"];
        return;
    }
    
    if (pasahitza.length < 5) {
        [ZAActivityBar showErrorWithStatus:@"Pasahitzak 6 karaktere eduki behar ditu gutxienez"];
        return;
    }
    
    if (![pasahitza isEqualToString:pasahitza2]) {
        [ZAActivityBar showErrorWithStatus:@"Pasahitza ez da berdina"];
        return;
    }
    
    if (![MintzatuAPIClient isValidEmail:posta]) {
        [ZAActivityBar showErrorWithStatus:@"Posta ez da zuzena"];
        return;
    }
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:ezizena forKey:@"name"];
    [params setValue:ezizena forKey:@"username"];
    [params setValue:posta forKey:@"email"];
    [params setValue:pasahitza forKey:@"password"];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [params setValue:[defaults stringForKey:@"APN_UID"] forKey:@"uuid"];
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Erregistratzen...";
    
    void (^block)() = ^(){
        [hud hide:YES];
    };
    
    [[MintzatuAPIClient sharedClient] postPath:@"register" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        block();
        NSInteger error = [[responseObject objectForKey:@"error"] integerValue];
        if (error == 0) {
            [MintzatuAPIClient saveUserId:[responseObject objectForKey:@"id"]];
            [MintzatuAPIClient saveUserToken:[responseObject objectForKey:@"token"]];
            [MintzatuAPIClient saveUserFullname:[responseObject objectForKey:@"fullname"]];
            AppDelegate *delegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
            [delegate loadMainController];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        block();
    }];
}

#pragma mark TextField

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.tag == 0) {
        [((DetailCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0]]).detailTextField becomeFirstResponder];
    } else if (textField.tag == 1) {
        [((DetailCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:2 inSection:0]]).detailTextField becomeFirstResponder];
    } else if (textField.tag == 2) {
        UITextField *textField = ((DetailCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:3 inSection:0]]).detailTextField;
        textField.returnKeyType = UIReturnKeyGo;
        [textField becomeFirstResponder];
    } else if (textField.tag == 3) {
        [self registerUser:nil];
    }
    return YES;
}

#pragma mark Social

- (void)socialObserver:(NSNotification*)notification
{
    NSDictionary *dict = [notification userInfo];
    BOOL granted = [[dict objectForKey:SocialAccountGrantedKey] boolValue];
    BOOL userLogged = [[dict objectForKey:SocialAccountUserLoggedKey] boolValue];
    
    if (!granted || !userLogged) {
        NSUInteger row;
        if ([[dict objectForKey:SocialAccountAccountTypeKey] isEqualToString:@"Facebook"]) {
            row = 0;
        } else {
            row = 1;
        }
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Abisua" message:@"Sistemaren ezarpenetan Twitter-a eta Facebook-a konfiguratu" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    } else {
        [self userFacebookData];
    }
}

- (void)userFacebookData
{
    ACAccountStore *accountStore = [[ACAccountStore alloc] init];
    ACAccountType *fbType = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierFacebook];
    
    SLRequestHandler requestHandler = ^(NSData *responseData, NSHTTPURLResponse *urlResponse, NSError *error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (responseData) {
                NSInteger statusCode = urlResponse.statusCode;
                NSDictionary *postResponseData = [NSJSONSerialization JSONObjectWithData:responseData
                                                                                 options:NSJSONReadingMutableContainers
                                                                                   error:NULL];
                if (statusCode >= 200 && statusCode < 300) {
                    _fbEmail = [postResponseData objectForKey:@"email"];
                    _fbUsername = [postResponseData objectForKey:@"username"];
                    [self.tableView reloadData];
                } else {
                    NSLog(@"[ERROR] Server responded: status code %d %@", statusCode, [postResponseData debugDescription]);
                    [self showFacebookFetchDataErrorAlert];
                }
            } else {
                NSLog(@"[ERROR] An error occurred while posting: %@", [error localizedDescription]);
                [self showFacebookFetchDataErrorAlert];
            }
            [_hud hide:YES];
        });
    };
    
    ACAccountStoreRequestAccessCompletionHandler accountStoreHandler = ^(BOOL granted, NSError *error) {
        if (granted) {
            NSArray *accounts = [accountStore accountsWithAccountType:fbType];
            ACAccount *account = [accounts lastObject];
            ACAccountCredential *fbCredential = [account credential];
            NSString *accessToken = [fbCredential oauthToken];
            NSURL *url = [NSURL URLWithString:@"https://graph.facebook.com/me"];
            
            NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
            [params setValue:accessToken forKey:@"access_token"];
            [params setValue:@"username, email" forKey:@"fields"];
            SLRequest *request = [SLRequest requestForServiceType:SLServiceTypeFacebook
                                                    requestMethod:SLRequestMethodGET
                                                              URL:url
                                                       parameters:params];
            [request setAccount:[accounts lastObject]];
            [request performRequestWithHandler:requestHandler];
            _hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            _hud.labelText = @"Datuak jasotzen...";
        } else {
            NSLog(@"[ERROR] An error occurred while asking for user authorization: %@", [error localizedDescription]);
            [self showFacebookFetchDataErrorAlert];
        }
    };
    
    NSDictionary *options = @{
                              @"ACFacebookAppIdKey" : @"626644270708710",
                              @"ACFacebookPermissionsKey" : @[@"basic_info"],
                              @"ACFacebookAudienceKey" : ACFacebookAudienceEveryone}; // Needed only when write permissions are requested
    
    [accountStore requestAccessToAccountsWithType:fbType options:options completion:accountStoreHandler];

}

- (void)showFacebookFetchDataErrorAlert
{
    
}

#pragma mark TableView

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0)
        return 4;
    return 1;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        DetailCell *cell = [tableView dequeueReusableCellWithIdentifier:@"RegisterCell"];
        NSString *info, *placeholder, *text;
        switch (indexPath.row) {
            case 0:
                info = @"posta";
                placeholder = @"posta elektronikoa";
                if (_fbEmail != nil) {
                    text = _fbEmail;
                }
                cell.type = SimpleCellTypeTop;
                break;
            case 1:
                info = @"ezizena";
                placeholder = @"ezizena";
                if (_fbUsername != nil) {
                    text = _fbUsername;
                }
                cell.type = SimpleCellTypeMiddle;
                break;
            case 2:
                info = @"pasahitza";
                placeholder = @"******";
                cell.type = SimpleCellTypeMiddle;
                cell.detailTextField.secureTextEntry = YES;
                break;
            case 3:
                info = @"pasahitza";
                placeholder = @"******";
                cell.type = SimpleCellTypeBottom;
                cell.detailTextField.secureTextEntry = YES;
                break;
                
            default:
                break;
        }
        
        cell.borderColor = [UIColor backgroundBeige];
        cell.borderWidth = 1.0f;
        
        cell.infoLabel.text = info;
        cell.detailTextField.placeholder = placeholder;
        cell.detailTextField.tag = indexPath.row;
        cell.detailTextField.delegate = self;
        
        if (text != nil) {
            cell.detailTextField.text = text;
        }
        
        return cell;
    } else {
        RegisterButtonCell *cell = [tableView dequeueReusableCellWithIdentifier:@"RegisterButtonCell"];
        [cell.registerButton addTarget:self action:@selector(registerUser:) forControlEvents:UIControlEventTouchUpInside];
        return cell;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 10.0f;
}

@end
