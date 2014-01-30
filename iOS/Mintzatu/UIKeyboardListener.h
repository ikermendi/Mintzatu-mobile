//
//  UIKeyboardListener.h
//  MintzatuLogin
//
//  Created by Iker Mendilibar Fernandez on 16/04/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UIKeyboardListener : NSObject
@property (nonatomic, assign) BOOL visible;
+ (UIKeyboardListener*) shared;
-(void) noticeShowKeyboard:(NSNotification *)inNotification;
-(void) noticeHideKeyboard:(NSNotification *)inNotification;
@end
