//
//  MintzatuAPI.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "MintzatuAPIClient.h"

#import "AFJSONRequestOperation.h"
#import "ZAActivityBar.h"
#import "AppDelegate.h"

NSString * const kBaseURL = @"http://www.mintzatu.com";

static NSString * const kMintzatuTokenKey = @"MintzatuToken";
static NSString * const kMintzatuFullnameKey = @"Mintzatufullname";
static NSString * const kMintzatuUserIdKey = @"MintzatuUserId";

@implementation MintzatuAPIClient

+ (MintzatuAPIClient *)sharedClient {
    static MintzatuAPIClient *_sharedClient = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSString * const kAFAppDotNetAPIBaseURLString = [NSString stringWithFormat:@"%@/api/", kBaseURL];
        _sharedClient = [[MintzatuAPIClient alloc] initWithBaseURL:[NSURL URLWithString:kAFAppDotNetAPIBaseURLString]];
    });
    
    return _sharedClient;
}

- (id)initWithBaseURL:(NSURL *)url {
    self = [super initWithBaseURL:url];
    if (!self) {
        return nil;
    }
    
    [self registerHTTPOperationClass:[AFJSONRequestOperation class]];
    
    // Accept HTTP Header; see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1
	[self setDefaultHeader:@"Accept" value:@"application/json"];
    self.defaultSSLPinningMode = AFSSLPinningModeNone;
    return self;
}

- (void)postPath:(NSString *)path
      parameters:(NSDictionary *)parameters
         success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success
         failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
	[super postPath:path parameters:parameters success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSInteger errorCode = [self error:responseObject];
        if (errorCode != 0) {
            NSError *error = [NSError errorWithDomain:@"MintzatuAPI" code:errorCode userInfo:nil];
            [ZAActivityBar setLocationTabBar];
            [ZAActivityBar showErrorWithStatus:[self handleErrorWithCode:error.code] duration:3.0f];
            if (failure != nil) {
                failure(operation, error);
            }
        } else {
            if (success != nil) {
                success(operation, responseObject);
            }
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [ZAActivityBar setLocationTabBar];
        [ZAActivityBar showErrorWithStatus:@"Network error" duration:2.0f];
        
        if (failure != nil) {
            failure(operation, error);
        }
    }];
}

#pragma mark Session Helpers

+ (void)saveUserToken:(NSString*)token
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:token forKey:kMintzatuTokenKey];
    [defaults synchronize];

}

+ (NSString*)userToken
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults objectForKey:kMintzatuTokenKey];
}

+ (void)saveUserId:(NSString*)userId
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:userId forKey:kMintzatuUserIdKey];
    [defaults synchronize];
}

+ (NSString*)userId
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults objectForKey:kMintzatuUserIdKey];
}

+ (void)saveUserFullname:(NSString*)fullname
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:fullname forKey:kMintzatuFullnameKey];
    [defaults synchronize];
}

+ (NSString*)fullname
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults objectForKey:kMintzatuFullnameKey];
}


+ (BOOL)isLogged
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults objectForKey:kMintzatuUserIdKey] == NULL ? NO : YES;
}

+ (void)logout
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:NULL forKey:kMintzatuTokenKey];
    [defaults setObject:NULL forKey:kMintzatuUserIdKey];
    [defaults setObject:NULL forKey:kMintzatuFullnameKey];
    [defaults synchronize];
    
    FBSession *session = [[FBSession alloc] init];
    if (session.state == FBSessionStateCreatedTokenLoaded) {
        [FBSession.activeSession closeAndClearTokenInformation];
        FBSession.activeSession = nil;
    }
}

#pragma mark Error helper

- (NSInteger)error:(id)json
{
    NSInteger error = [[json objectForKey:@"error"] intValue];
    return error;
}

- (NSString*)handleErrorWithCode:(NSInteger)errorCode
{
     NSString *errorString;
     if (errorCode == -1) {
     errorString = @"Erabiltzailea ez da existitzen";
     } else if (errorCode == -3) {
     errorString = @"Pasahitza edo posta gaizki dago";
     } else if (errorCode == -600) {
     errorString = @"Posta erregistratuta dago";
     } else if (errorCode == -601) {
     errorString = @"Erabiltzailea erregistratuta dago";
     } else if (errorCode == -602) {
     errorString = @"Pasahitzak 6 karaktere eduki behar ditu gutxienez";
     } else if (errorCode == -666) {
     errorString = @"Leku hau Mintzatu sarearen parte da jada";
     } else if (errorCode == -801) {
     errorString = @"Posta ez da existitzen";
     } else if (errorCode == -10) {
     errorString = @"Zure saioa amaitu egin da. Saioa berriro hasi";
     [MintzatuAPIClient logout];
     AppDelegate *appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
     [appDelegate loadLoginController];
     } else {
         errorString = [NSString stringWithFormat:@"Errore bat egon da, saiatu berriro."];
         //errorString = [NSString stringWithFormat:@"%d - Errore bat egon da, saiatu berriro.",errorCode];
         NSLog(@"ErrorCode= %d",errorCode);
     }
     return errorString;

}

#pragma mark Other helpers

+ (BOOL)isValidEmail:(NSString *)checkString
{
    BOOL stricterFilter = YES; // Discussion http://blog.logichigh.com/2010/09/02/validating-an-e-mail-address/
    NSString *stricterFilterString = @"[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}";
    NSString *laxString = @".+@([A-Za-z0-9]+\\.)+[A-Za-z]{2}[A-Za-z]*";
    NSString *emailRegex = stricterFilter ? stricterFilterString : laxString;
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    return [emailTest evaluateWithObject:checkString];
}

@end
