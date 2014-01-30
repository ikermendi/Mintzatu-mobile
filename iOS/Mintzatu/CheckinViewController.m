//
//  CheckinViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 09/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "CheckinViewController.h"

#import <Social/Social.h>
#import <Accounts/Accounts.h>

#import "AFNetworking.h"
#import "MintzatuAPIClient.h"
#import "SocialAccountWrapper.h"
#import "JSFlatButton.h"
#import "Place.h"
#import "PlaceCategory.h"
#import "Badge.h"
#import "UIImageView+WebCache.h"
#import "MBProgressHUD.h"
#import "UIPlaceHolderTextView.h"
#import "ZAActivityBar.h"
#import "SocialEzarpenakViewController.h"


@interface CheckinViewController () <UIActionSheetDelegate, UITextViewDelegate, UIAlertViewDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate>
{
    SocialAccountWrapper *_socialWrapper;
    MBProgressHUD *_hud;
    BOOL _canShare;
    BOOL _imagePicked;
    __weak IBOutlet UITextView *_lekuaStreet;
    __weak IBOutlet UILabel *_remainCharactersTextField;
}
@property (weak, nonatomic) IBOutlet JSFlatButton *checkinButton;
@property (weak, nonatomic) IBOutlet UISwitch *shareSwitch;
@property (weak, nonatomic) IBOutlet UIImageView *uploadPhotoImageView;
@property (weak, nonatomic) IBOutlet UIPlaceHolderTextView *commentTextView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UIImageView *photoImageView;
@end

@implementation CheckinViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        _socialWrapper = [[SocialAccountWrapper alloc] init];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
        self.screenName = @"CheckinViewController";
    _imagePicked = NO;
    _commentTextView.placeholder = @"Iruzkina idatzi";
    
    [self.checkinButton setButtonBackgroundColor:[UIColor mintzatuBlue]];
    [self.checkinButton setButtonForegroundColor:[UIColor mintzatuBlue]];
    [self.checkinButton setTitle:@"Check-in" forState:UIControlStateNormal];
    [self.checkinButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.checkinButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
    [self.checkinButton addTarget:self action:@selector(postCheckin) forControlEvents:UIControlEventTouchUpInside];
    
    _nameLabel.text = _place.izena;
    [_photoImageView setImageWithURL:[NSURL URLWithString:_place.irudia]];
    
    _lekuaStreet.text = _place.helbidea;
    _lekuaStreet.contentInset = UIEdgeInsetsMake(-4, -4, 0, 0);
    _lekuaStreet.textColor = [UIColor whiteColor];
    _lekuaStreet.font = [UIFont fontWithName:@"Helvetica-Bold" size:11.0f];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    _canShare = [_socialWrapper isTwitterActivated];
    if (!_canShare) {
        _canShare = [_socialWrapper isFBActivated];
    }
    
    if (_canShare) {
        _shareSwitch.on = YES;
    } else {
        _shareSwitch.on = NO;
    }
}


- (void)dealloc
{
    self.shareSwitch = nil;
    self.checkinButton = nil;
    self.uploadPhotoImageView = nil;
    self.commentTextView = nil;
    self.nameLabel = nil;
    self.photoImageView = nil;
    self.place = nil;
    _shareSwitch = nil;
}

#pragma mark Share switch

- (IBAction)shareSwitchChanged:(id)sender
{
    if (!_canShare && _shareSwitch.on) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Abisua" message:@"Sare sozial ezarpenak konfiguratu" delegate:self cancelButtonTitle:@"Ezeztatu" otherButtonTitles:@"Ok", nil];
        [alert show];
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 0) {
        _shareSwitch.on = NO;
        return;
    } else {
        [self performSegueWithIdentifier:@"socialezarpenak" sender:nil];
    }
}

#pragma mark textField delegate

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    
    if (textView.text.length == 140) {
        return NO;
    }
    
    _remainCharactersTextField.text = [NSString stringWithFormat:@"%d", 139 - textView.text.length];
    
    return YES;
}

#pragma mark Media Picker

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    [picker dismissViewControllerAnimated:YES completion:nil];
    UIImage *selectedImage = [info objectForKey:UIImagePickerControllerOriginalImage];
    _uploadPhotoImageView.contentMode = UIViewContentModeScaleAspectFill;
    _uploadPhotoImageView.image = selectedImage;
    [_uploadPhotoImageView setNeedsDisplay];
    _imagePicked = YES;
}

- (IBAction)selectImage:(id)sender
{
    UIActionSheet *actionSheet = [[UIActionSheet alloc]
                                  initWithTitle:@"Argazkia aukeratu"
                                  delegate:self
                                  cancelButtonTitle:@"Cancelar"
                                  destructiveButtonTitle:nil
                                  otherButtonTitles:@"Galería", @"Cámara", nil];
    [actionSheet showFromTabBar:[[self tabBarController] tabBar]];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    if (buttonIndex == 0) {
        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    } else if (buttonIndex == 1) {
        if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            picker.sourceType = UIImagePickerControllerSourceTypeCamera;
        } else {
            picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        }
    } else {
        [actionSheet dismissWithClickedButtonIndex:actionSheet.cancelButtonIndex animated:YES];
        return;
    }
    
    picker.delegate = self;
    picker.allowsEditing = NO;
    [self presentViewController:picker animated:YES completion:nil];
}

- (void)navigationController:(UINavigationController *)navController willShowViewController:(UIViewController *)picker animated:(BOOL)animated
{
    picker.navigationItem.title = @"";
}

#pragma mark Checkin

- (void)postCheckin
{     
    _hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    _hud.labelText = @"Check-in egiten";
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:_place.idLekua forKey:@"idPlace"];
    [params setValue:_commentTextView.text forKey:@"comment"];

    NSData *imageData = UIImageJPEGRepresentation(_uploadPhotoImageView.image, 0.5);
    NSURLRequest *request = [[MintzatuAPIClient sharedClient] multipartFormRequestWithMethod:@"POST" path:@"checkin" parameters:params constructingBodyWithBlock: ^(id <AFMultipartFormData> formData) {
        if (_imagePicked) {
            [formData appendPartWithFileData:imageData name:@"image" fileName:@"image.jpeg" mimeType:@"image/jpeg"];
        }
    }];
    
    AFJSONRequestOperation *operation = [[AFJSONRequestOperation alloc] initWithRequest:request];
    [operation setUploadProgressBlock:^(NSUInteger bytesWritten, long long totalBytesWritten, long long totalBytesExpectedToWrite) {
        NSLog(@"Sent %lld of %lld bytes", totalBytesWritten, totalBytesExpectedToWrite);
    }];
    
    __weak AFJSONRequestOperation *weakOperation = operation;
	[operation setCompletionBlock:^{
		NSDictionary *json = weakOperation.responseJSON;
        NSInteger error = [[json objectForKey:@"error"] integerValue];
        dispatch_async(dispatch_get_main_queue(), ^{
            [_hud hide:YES];
            if (error == -202) {
                [ZAActivityBar setLocationTabBar];
                [ZAActivityBar showErrorWithStatus:@"Ezin duzu check-in egin" duration:2.0f];
            } else {
                if (_canShare) {
                    if ([_socialWrapper isTwitterActivated])
                        [self postTwitter];
                    
                    if ([_socialWrapper isFBActivated]) {
                        [self postFacebook];
                    }
                }
                
                Badge *badge = nil;
                NSArray *badges = [json objectForKey:@"badges"];
                if (![badges isEqual:[NSNull null]]) {
                    NSDictionary *badgeDict = [badges objectAtIndex:0];
                    badge = [[Badge alloc] initWithDictionary:badgeDict];
                }
 
                [self.navigationController popViewControllerAnimated:YES];
                [self.delegate reloadViewWithBadge:badge];
            }
        });
        
	}];
    
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [queue addOperation:operation];
}

- (void)back
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_hud hide:YES];
        [self.navigationController popViewControllerAnimated:YES];
    });
}

#pragma mark Social

- (void)postTwitter
{
    ACAccountStore *accountStore = [[ACAccountStore alloc] init];
    ACAccountType *twitterType = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierTwitter];
    
    SLRequestHandler requestHandler = ^(NSData *responseData, NSHTTPURLResponse *urlResponse, NSError *error) {
        if (responseData) {
            NSInteger statusCode = urlResponse.statusCode;
            if (statusCode >= 200 && statusCode < 300) {
                NSDictionary *postResponseData = [NSJSONSerialization JSONObjectWithData:responseData
                                                                                 options:NSJSONReadingMutableContainers
                                                                                   error:NULL];
                NSLog(@"[SUCCESS!] Created Tweet with ID: %@", postResponseData[@"id_str"]);
            } else {
                NSLog(@"[ERROR] Server responded: status code %d %@", statusCode, [NSHTTPURLResponse localizedStringForStatusCode:statusCode]);
            }
        } else {
            NSLog(@"[ERROR] An error occurred while posting: %@", [error localizedDescription]);
        }
        
        [self back];
    };
    
    ACAccountStoreRequestAccessCompletionHandler accountStoreHandler = ^(BOOL granted, NSError *error) {
        if (granted) {
            //NSArray *accounts = [accountStore accountsWithAccountType:twitterType];
            NSURL *url = [NSURL URLWithString:@"https://api.twitter.com/1.1/statuses/update.json"];
            NSDictionary *params = @{@"status" : [self twitterStatus]};
            SLRequest *request = [SLRequest requestForServiceType:SLServiceTypeTwitter
                                                    requestMethod:SLRequestMethodPOST
                                                              URL:url
                                                       parameters:params];
            //[request setAccount:[accounts lastObject]];
            //Comprobar post Twitter -> Correcto/Funciona
            ACAccount *t = [_socialWrapper getSelectedTwitterAcount];
            if(t){
                [request setAccount:t];
                [request performRequestWithHandler:requestHandler];
            }else{
                //a borrado la cuenta de twitter
//                [ZAActivityBar setLocationTabBar];
//                [ZAActivityBar showErrorWithStatus:@"Twitter kontua ezabatu egin da. Kontu bat sartu lehenengoz." duration:2.0f];
                NSLog(@"User erase Twitter account.");
            }
            
        } else {
            NSLog(@"[ERROR] An error occurred while asking for user authorization: %@", [error localizedDescription]);
            
        }
    };
    
    [accountStore requestAccessToAccountsWithType:twitterType options:NULL completion:accountStoreHandler];
}

- (void)postFacebook
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
                    NSLog(@"[SUCCESS!] Created feed with ID: %@", postResponseData[@"id_str"]);
                } else {
                    NSLog(@"[ERROR] Server responded: status code %d %@", statusCode, [postResponseData debugDescription]);
                }
            } else {
                NSLog(@"[ERROR] An error occurred while posting: %@", [error localizedDescription]);
            }
            
            [self back];
        });
    };
    
    ACAccountStoreRequestAccessCompletionHandler accountStoreHandler = ^(BOOL granted, NSError *error) {
        if (granted) {
            NSArray *accounts = [accountStore accountsWithAccountType:fbType];
            ACAccount *account = [accounts lastObject];
            ACAccountCredential *fbCredential = [account credential];
            NSString *accessToken = [fbCredential oauthToken];
            NSURL *url = [NSURL URLWithString:@"https://graph.facebook.com/me/feed"];
            NSMutableDictionary *params = [self facebookData];
            [params setValue:accessToken forKey:@"access_token"];
            SLRequest *request = [SLRequest requestForServiceType:SLServiceTypeFacebook
                                                    requestMethod:SLRequestMethodPOST
                                                              URL:url
                                                       parameters:params];
            [request setAccount:[accounts lastObject]];
            [request performRequestWithHandler:requestHandler];
        } else {
            NSLog(@"[ERROR] An error occurred while asking for user authorization: %@", [error localizedDescription]);
            [self back];
        }
    };
    
    NSDictionary *options = @{
                              @"ACFacebookAppIdKey" : @"626644270708710",
                              @"ACFacebookPermissionsKey" : @[@"basic_info"],
                              @"ACFacebookAudienceKey" : ACFacebookAudienceEveryone}; // Needed only when write permissions are requested
    
    [accountStore requestAccessToAccountsWithType:fbType options:options completion:accountStoreHandler];
}

- (NSString*)twitterStatus
{
    //Hemen nago euskaraz mintzatzen! La roca del fraile :: Taberna #mintzatu #euskara http://lander.irontec.com/Mintzatu/lekuak/ikusi/lekua/la_roca_del_fraile
    PlaceCategory *cat = [_place.kategoriak objectAtIndex:0];
    return [NSString stringWithFormat:@"Hemen nago euskaraz mintzatzen! %@ :: %@ #mintzatu #euskara #mundiala %@/lekuak/ikusi/lekua/%@", _place.izena, cat.izena, kBaseURL, _place.url];
}

- (NSMutableDictionary*)facebookData
{
    PlaceCategory *cat = [_place.kategoriak objectAtIndex:0];
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[NSString stringWithFormat:@"%@/lekuak/ikusi/lekua/%@", kBaseURL, _place.url] forKey:@"link"];
    [params setValue:[NSString stringWithFormat:@"%@/lekuak/ikusi/lekua/%@", kBaseURL, _place.url] forKey:@"caption"];
    [params setValue:_place.irudia forKey:@"picture"];
    [params setValue:[NSString stringWithFormat:@"%@ :: %@ :: Mintzatu", _place.izena, cat.izena] forKey:@"name"];
    [params setValue:@"Hemen nago euskaraz mintzatzen!" forKey:@"description"];

    return params;
}

@end
