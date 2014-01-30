//
//  Comment.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "Comment.h"

@implementation Comment

- (CGFloat)commentHeightWithWidth:(CGFloat)width
{
    CGSize size = [_iruzkina sizeWithFont:[UIFont fontWithName:@"Helvetica" size:13.0f] constrainedToSize:CGSizeMake(width, 1000)];
    return size.height;
}

@end
