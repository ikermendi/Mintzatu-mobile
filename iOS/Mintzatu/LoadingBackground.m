//
//  LoadingBackground.m
//  MintzatuLogin
//
//  Created by Iker Mendilibar Fernandez on 16/04/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LoadingBackground.h"

@interface LoadingBackground ()
{
    CGFloat _alpha;
}
@end

@implementation LoadingBackground

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.opaque = NO;
        self.alpha = 0.0f;
        _alpha = 0.5;
    }
    
    return self;
}

- (id)initWithFrame:(CGRect)frame alpha:(CGFloat)alpha
{
    self = [super initWithFrame:frame];
    if (self) {
        self.opaque = NO;
        self.alpha = 0.0;
        _alpha = alpha;
    }
    
    return self;
}

- (void)drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    UIGraphicsBeginImageContextWithOptions(self.bounds.size, YES, 1);
	// Our gradient only has two locations - start and finish. More complex gradients might have more colours
    size_t num_locations = 2;
	// The location of the colors is at the start and end
    CGFloat locations[2] = { 0.0, 1.0 };
	// These are the colors! That's two RBGA values
    CGFloat components[8] = {
        0.4,0.4,0.4, _alpha + 3,
        0.1,0.1,0.1, _alpha };
	// Create a color space
    CGColorSpaceRef myColorspace = CGColorSpaceCreateDeviceRGB();
	// Create a gradient with the values we've set up
    CGGradientRef myGradient = CGGradientCreateWithColorComponents (myColorspace, components, locations, num_locations);
	// Set the radius to a nice size, 80% of the width. You can adjust this
    float myRadius = (self.bounds.size.width*.8)/2;
	// Now we draw the gradient into the context. Think painting onto the canvas
    CGContextDrawRadialGradient (context, myGradient, self.center, 0, self.center, myRadius, kCGGradientDrawsAfterEndLocation);
    
    CGContextSetAlpha(context, 0.4);
    
    //CGContextAddPath(context, roundRectPath);
    CGContextFillPath(context);
    CGGradientRelease(myGradient);
    CGColorSpaceRelease(myColorspace);
}


@end
