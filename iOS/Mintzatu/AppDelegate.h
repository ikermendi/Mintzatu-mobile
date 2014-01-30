//
//  AppDelegate.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 19/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import <FacebookSDK/FacebookSDK.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>
@property (strong, nonatomic) UIWindow *window;
- (void)loadMainController;
- (void)loadLoginController;
- (BOOL)canCheckinCoordinate:(CLLocationCoordinate2D)coordinate;
@end
