//
//  SocialAccountWrapper.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 30/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <Accounts/Accounts.h>
#import <Social/Social.h>

extern NSString *const SocialAccountGrantedKey;
extern NSString *const SocialAccountAccountTypeKey;
extern NSString *const SocialAccountUserLoggedKey;

@interface SocialAccountWrapper : NSObject
- (void)registerObserver:(id)observer selector:(SEL)selector;
- (void)unregisterObserver:(id)observer;
- (void)unregisterAllObservers;

//Facebook
- (void)askForFBBasicPermission;
- (BOOL)isFBActivated;
- (void)deactivateFB;

//Twitter
- (BOOL)isTwitterActivated;
- (void)deactivateTwitter;
- (void)setSelectedTwitterAcount:(NSString *)identifier;
- (ACAccount *)getSelectedTwitterAcount;
- (void)askForTwitterBasicPermission;

@end
