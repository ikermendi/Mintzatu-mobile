//
//  AppDelegate.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 19/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "AppDelegate.h"
#import "MintzatuAPIClient.h"
#import "GAI.h"

@interface AppDelegate () <CLLocationManagerDelegate>
{
    CLLocationManager *_locationManager;
    CLLocation *_userLocation;
    
}
@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    #if !TARGET_IPHONE_SIMULATOR
        [application registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound];
    #else
        Byte *ptr = malloc(8 * sizeof(Byte));
        memset(ptr, 0, 8);
        [self application:nil didRegisterForRemoteNotificationsWithDeviceToken:[[NSData alloc] initWithBytes:ptr length:100]];
        free(ptr);
    #endif
    
    [self customization];
    
    if ([MintzatuAPIClient isLogged]) {
        [self loadMainController];
    }
    
    //Google Analytics
    // Optional: automatically send uncaught exceptions to Google Analytics.
    [GAI sharedInstance].trackUncaughtExceptions = YES;
    
    // Optional: set Google Analytics dispatch interval to e.g. 20 seconds.
    [GAI sharedInstance].dispatchInterval = 20;
    
    // Optional: set Logger to VERBOSE for debug information.
    [[[GAI sharedInstance] logger] setLogLevel:kGAILogLevelVerbose];
    
    // Initialize tracker.
    id<GAITracker> tracker = [[GAI sharedInstance] trackerWithTrackingId:@"UA-30217319-2"];
    

        
	return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    [_locationManager stopUpdatingLocation];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    if (_locationManager == nil) {
        _locationManager = [[CLLocationManager alloc] init];
        _locationManager.delegate = self;
    }
    [_locationManager startUpdatingLocation];

}



- (void)applicationWillTerminate:(UIApplication *)application
{
    
}


#pragma mark Location manager

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    _userLocation = [locations lastObject];
}

- (BOOL)canCheckinCoordinate:(CLLocationCoordinate2D)coordinate
{
    if (_userLocation == nil)
        return NO;
    
    CLLocation *placeLocation = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
    if ([_userLocation distanceFromLocation:placeLocation] / 1000 < MAX_CHECKIN_DISTANCE) {
        return YES;
    } else {
        return NO;
    }
}

#pragma mark Storyboard control
    
- (void)loadMainController
{
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:[NSBundle mainBundle]];
    UIViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"MainController"];
    
    self.window.rootViewController = controller;
}

- (void)loadLoginController
{
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:[NSBundle mainBundle]];
    UIViewController *controller = [storyboard instantiateViewControllerWithIdentifier:@"LoginController"];
    
    self.window.rootViewController = controller;
}

#pragma mark Remote notifications register

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    
    const unsigned *tokenBytes = [deviceToken bytes];
    NSString *regId = [NSString stringWithFormat:@"%08x%08x%08x%08x%08x%08x%08x%08x",
                       ntohl(tokenBytes[0]), ntohl(tokenBytes[1]), ntohl(tokenBytes[2]),
                       ntohl(tokenBytes[3]), ntohl(tokenBytes[4]), ntohl(tokenBytes[5]),
                       ntohl(tokenBytes[6]), ntohl(tokenBytes[7])];
    
    NSLog(@"Did register for remote notifications: %@", regId);
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setValue:regId forKey:@"APN_UID"];
    [defaults synchronize];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"Fail to register for remote notifications: %@", error);
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setValue:@"NO_PUSH_API" forKey:@"APN_UID"];
    [defaults synchronize];
}

#pragma mark Customization

- (void)customization
{
    [[UINavigationBar appearance] setBackgroundImage:[UIImage imageNamed:@"NavBar"] forBarMetrics:UIBarMetricsDefault];
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    
    if (!SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
        [[UIBarButtonItem appearance] setTintColor:[UIColor mintzatuBlue]];
        [[UINavigationBar appearance] setTintColor:[UIColor mintzatuBlue]];
        
    } else {
        self.window.tintColor = [UIColor whiteColor];
        [[UITabBar appearance] setTintColor:[UIColor mintzatuBlue]];
    }
    
}

@end
