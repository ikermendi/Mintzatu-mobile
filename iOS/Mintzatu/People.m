//
//  People.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "People.h"

@implementation People

- (void)setValue:(id)value forKey:(NSString *)key
{
    if ([key isEqualToString:@"id"]) {
        self.identifier = value;
    } else {
        [super setValue:value forKey:key];
    }
}

@end
