//
//  LekuaInfoImageView.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 04/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaInfoImageView.h"

@implementation LekuaInfoImageView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
	UITouch *touch = [touches anyObject];
    
	NSUInteger tapCount = touch.tapCount;
	switch (tapCount) {
		case 1:
            [self singleTapHandler];
            break;
		default:
			break;
	}
    
	[[self nextResponder] touchesEnded:touches withEvent:event];
}

- (void) singleTapHandler {

}

@end
