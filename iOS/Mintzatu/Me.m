//
//  Me.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "Me.h"

@implementation Me

- (void)setValue:(id)value forKey:(NSString *)key
{
    if ([key isEqualToString:@"id"]) {
        self.identifier = value;
    } else if ([key isEqualToString:@"badgets"]) {
        self.badges = value;
    } else {
        [super setValue:value forKey:key];
    }
}

@end
