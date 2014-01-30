//
//  Category.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "PlaceCategory.h"

@implementation PlaceCategory

- (void)setValue:(id)value forKey:(NSString *)key
{
    if ([key isEqualToString:@"id_kategoria"]) {
        self.identifier = value;
    } else {
        [super setValue:value forKey:key];
    }
}

@end
