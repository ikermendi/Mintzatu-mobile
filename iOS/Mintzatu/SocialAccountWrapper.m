//
//  SocialAccountWrapper.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 30/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SocialAccountWrapper.h"

NSString *const SocialAccountNotification = @"SocialAccountNotification";
NSString *const SocialAccountGrantedKey = @"Granted";
NSString *const SocialAccountAccountTypeKey = @"AccountType";
NSString *const SocialAccountUserLoggedKey = @"UserLogged";

@interface SocialAccountWrapper ()
{
    NSMutableArray *_observers;
}
@end

@implementation SocialAccountWrapper

- (id)init
{
    self = [super init];
    if (self) {
        _observers = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)registerObserver:(id)observer selector:(SEL)selector
{
    if ([_observers containsObject:observer]) {
        return;
    } else {
        [_observers addObject:observer];
    }
    
    [[NSNotificationCenter defaultCenter] addObserver:observer selector:selector name:SocialAccountNotification object:nil];
}

- (void)unregisterObserver:(id)observer
{
    for (id observer in _observers) {
        if ([_observers containsObject:observer]) {
            [[NSNotificationCenter defaultCenter] removeObserver:observer];
            return;
        }
    }
}

- (void)unregisterAllObservers
{
    for (id observer in _observers) {
        [[NSNotificationCenter defaultCenter] removeObserver:observer];
    }
}

- (void)dealloc
{
    _observers = nil;
}

#pragma mark FB

- (BOOL)isFBActivated
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:@"FBLogged"];
}

- (void)deactivateFB
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:NO forKey:@"FBLogged"];
    [defaults synchronize];
}

- (void)askForFBBasicPermission
{
    NSDictionary *options = @{
                              @"ACFacebookAppIdKey" : @"626644270708710",
                              @"ACFacebookPermissionsKey" : @[@"basic_info, email"],
                              @"ACFacebookAudienceKey" : ACFacebookAudienceEveryone}; // Needed only when write permissions are requested
    
    // Initialize the account store
    ACAccountStore *accountStore = [[ACAccountStore alloc] init];
    
    // Get the Facebook account type for the access request
    ACAccountType *fbAccountType = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierFacebook];
    
    // Request access to the Facebook account with the access info
    [accountStore requestAccessToAccountsWithType:fbAccountType
                                          options:options
                                       completion:^(BOOL granted, NSError *error) {
                                           NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
                                           
                                           if (granted == NO) {
                                               NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                                               [defaults setBool:granted forKey:@"FBLogged"];
                                               [defaults synchronize];
                                               
                                               [dict setValue:[NSNumber numberWithBool:granted] forKey:SocialAccountGrantedKey];
                                               [dict setValue:@"Facebook" forKey:SocialAccountAccountTypeKey];
                                               // Esto antes tenia sentido ahora no. Antes se miraba el error.code
                                               [dict setValue:NO forKey:SocialAccountUserLoggedKey];
                                               dispatch_async(dispatch_get_main_queue(), ^{
                                                   [[NSNotificationCenter defaultCenter] postNotificationName:SocialAccountNotification object:nil userInfo:dict];
                                               });
                                           } else {
                                               [self askForFBPostPermission];
                                           }
                                       }];
}

- (void)askForFBPostPermission
{
    NSDictionary *options = @{
                              @"ACFacebookAppIdKey" : @"626644270708710",
                              @"ACFacebookPermissionsKey" : @[@"publish_actions"],
                              @"ACFacebookAudienceKey" : ACFacebookAudienceEveryone}; // Needed only when write permissions are requested
    
    // Initialize the account store
    ACAccountStore *accountStore = [[ACAccountStore alloc] init];
    
    // Get the Facebook account type for the access request
    ACAccountType *fbAccountType = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierFacebook];
    
    // Request access to the Facebook account with the access info
    [accountStore requestAccessToAccountsWithType:fbAccountType
                                          options:options
                                       completion:^(BOOL granted, NSError *error) {
                                           NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
                                           
                                           NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                                           [defaults setBool:granted forKey:@"FBLogged"];
                                           [defaults synchronize];
                                           
                                           [dict setValue:[NSNumber numberWithBool:granted] forKey:SocialAccountGrantedKey];
                                           [dict setValue:@"Facebook" forKey:SocialAccountAccountTypeKey];
                                           [dict setValue:error.code == 6 ? [NSNumber numberWithBool:NO]:[NSNumber numberWithBool:YES] forKey:SocialAccountUserLoggedKey];
                                           dispatch_async(dispatch_get_main_queue(), ^{
                                               [[NSNotificationCenter defaultCenter] postNotificationName:SocialAccountNotification object:nil userInfo:dict];
                                           });
                                       }];
}

#pragma mark Twitter

- (BOOL)isTwitterActivated
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults synchronize];
    return [defaults boolForKey:@"TwitterLogged"];
    [defaults synchronize];
}

- (void)deactivateTwitter
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:NO forKey:@"TwitterLogged"];
    [defaults setValue:nil forKey:@"TwitterIdentifier"];
    [defaults synchronize];
}

- (void)setSelectedTwitterAcount:(NSString *)identifier
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setValue:identifier forKey:@"TwitterIdentifier"];
    [defaults synchronize];
}

//Si devuelve nil significa que a borrado la cuenta de los ajustes de iOS, por lo que se desactiva Twitter en la aplicacion
- (ACAccount*)getSelectedTwitterAcount
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *identifier = [defaults objectForKey:@"TwitterIdentifier"];
    
    ACAccountStore *accountStore = [[ACAccountStore alloc] init];
    ACAccountType *twitterType = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierTwitter];
    NSArray *accounts = [accountStore accountsWithAccountType:twitterType];
    ACAccount *t;
    for(int x=0;x<accounts.count;x++){
        t = [accounts objectAtIndex:x];
        if([t.identifier isEqualToString:identifier]){
            return t;
        }
    }
    //quitar twitter activado
    [self deactivateTwitter];
    return nil;
}


- (void)askForTwitterBasicPermission
{
    ACAccountStore *accountStore = [[ACAccountStore alloc] init];
    ACAccountType *accountType = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierTwitter];
    [accountStore requestAccessToAccountsWithType:accountType
                                          options:nil
                                       completion:^(BOOL granted, NSError *error) {
                                
                                           BOOL logged = [SLComposeViewController isAvailableForServiceType:SLServiceTypeTwitter];
                                           if (!logged) {
                                               granted = NO;
                                           }
                                           
                                           NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                                           [defaults setBool:granted forKey:@"TwitterLogged"];
                                           [defaults synchronize];
                                           
                                           NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
                                           [dict setValue:[NSNumber numberWithBool:granted] forKey:SocialAccountGrantedKey];
                                           [dict setValue:@"Twitter" forKey:SocialAccountAccountTypeKey];
                                           [dict setValue:[NSNumber numberWithBool:YES] forKey:SocialAccountUserLoggedKey];
                                           dispatch_async(dispatch_get_main_queue(), ^{
                                               [[NSNotificationCenter defaultCenter] postNotificationName:SocialAccountNotification object:nil userInfo:dict];
                                           });
                                       }];
}


@end
