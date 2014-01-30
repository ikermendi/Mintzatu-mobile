//
//  BilatuTextField.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 17/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "BilatuTextField.h"

@implementation BilatuTextField

- (CGRect)textRectForBounds:(CGRect)bounds
{
   return CGRectInset(bounds , 10, 0);
}

- (CGRect)editingRectForBounds:(CGRect)bounds
{
    return CGRectInset(bounds , 10, 0);
}

@end
