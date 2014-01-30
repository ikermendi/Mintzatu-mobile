//
//  UIKeyboardListener.m
//  MintzatuLogin
//
//  Created by Iker Mendilibar Fernandez on 16/04/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "UIKeyboardListener.h"

@implementation UIKeyboardListener
static UIKeyboardListener *sListener;

+ (UIKeyboardListener*) shared {
	if ( nil == sListener ) sListener = [[UIKeyboardListener alloc] init];
	return sListener;
}

-(id) init {
	self = [super init];
	if ( self ) {
		NSNotificationCenter		*center = [NSNotificationCenter defaultCenter];
		[center addObserver:self selector:@selector(noticeShowKeyboard:) name:UIKeyboardDidShowNotification object:nil];
		[center addObserver:self selector:@selector(noticeHideKeyboard:) name:UIKeyboardWillHideNotification object:nil];
	}
	return self;
}

-(void) noticeShowKeyboard:(NSNotification *)inNotification {
	_visible = true;
}

-(void) noticeHideKeyboard:(NSNotification *)inNotification {
	_visible = false;
}

-(BOOL) isVisible {
	return _visible;
}
@end
