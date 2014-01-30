//
//  MintzatuAPI.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFHTTPClient.h"

extern NSString * const kBaseURL;

@interface MintzatuAPIClient : AFHTTPClient

+ (MintzatuAPIClient *)sharedClient;

+ (void)saveUserToken:(NSString*)token;
+ (NSString*)userToken;

+ (void)saveUserId:(NSString*)userId;
+ (NSString*)userId;

+ (void)saveUserFullname:(NSString*)fullname;
+ (NSString*)fullname;

+ (BOOL)isLogged;
+ (void)logout;

+ (BOOL)isValidEmail:(NSString *)checkString;

@end
