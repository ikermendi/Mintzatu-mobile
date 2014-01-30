//
//  DrawerView.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 03/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "DrawerView.h"

@implementation DrawerView
{
    CGFloat oldY;
    CGRect originalFrame;
    CGRect previousFrame;
    BOOL dragging;
    BOOL animatting;
    BOOL abort;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    originalFrame = self.frame;
    previousFrame = self.frame;
    abort = NO;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    previousFrame = self.frame;
    
    UITouch *touch = [[event allTouches] anyObject];
    CGPoint touchLocation = [touch locationInView:touch.view];
    dragging = YES;
    oldY = touchLocation.y;
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *touch = [[event allTouches] anyObject];
    CGPoint touchLocation = [touch locationInView:touch.view];
    if (dragging) {
        CGRect frame = touch.view.frame;
        CGFloat currentY = frame.origin.y;
        
        if (currentY < 120) {
            dragging = NO;
            touch.view.frame = originalFrame;
            UIView *viewToMove = [_delegate viewToMove];
            CGRect viewToMoveFrame = viewToMove.frame;
            viewToMoveFrame.origin = originalFrame.origin;
            viewToMoveFrame.origin.y += 20;
            viewToMove.frame = viewToMoveFrame;
        }
        
        frame.origin.y = touch.view.frame.origin.y + touchLocation.y - oldY;
        touch.view.frame = frame;
        if (_delegate != nil) {
            frame.origin.y += 20;
            [_delegate newRect:frame];
        }
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{

    dragging = NO;
    UITouch *touch = [[event allTouches] anyObject];
    CGRect frame = touch.view.frame;

    CGFloat currentY = frame.origin.y;
    
    if (currentY < 120) {
        animatting = YES;
        touch.view.frame = originalFrame;
        UIView *viewToMove = [_delegate viewToMove];
        CGRect viewToMoveFrame = viewToMove.frame;
        viewToMoveFrame.origin = originalFrame.origin;
        viewToMoveFrame.origin.y += 20;
        viewToMove.frame = viewToMoveFrame;
    }

    
    BOOL positive = frame.origin.y < previousFrame.origin.y ? YES : NO;
    if (positive == NO) {
        /*animatting = YES;
        [UIView animateWithDuration:1.0f animations:^{
            CGRect newFrame = CGRectMake(0, 347, frame.size.width, frame.size.height);
            touch.view.frame = newFrame;
            UIView *viewToMove = [_delegate viewToMove];
            CGRect viewToMoveFrame = viewToMove.frame;
            viewToMoveFrame.origin = newFrame.origin;
            viewToMoveFrame.origin.y += 20;
            viewToMove.frame = viewToMoveFrame;
        } completion:^(BOOL finished) {
            animatting = NO;
        }];*/
    } else {
        /*animatting = YES;
            touch.view.frame = originalFrame;
            UIView *viewToMove = [_delegate viewToMove];
            CGRect viewToMoveFrame = viewToMove.frame;
            viewToMoveFrame.origin = originalFrame.origin;
            viewToMoveFrame.origin.y += 20;
            viewToMove.frame = viewToMoveFrame;*/
        
    }
}

@end
