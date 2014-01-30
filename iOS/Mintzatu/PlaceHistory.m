//
//  PlaceHistory.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "PlaceHistory.h"

@interface PlaceHistory ()
@property (strong, nonatomic) NSNumber *type;
@end

@implementation PlaceHistory

- (void)setValue:(id)value forKey:(NSString *)key
{
    if ([key isEqualToString:@"id"]) {
        self.identifier = value;
    } else {
        [super setValue:value forKey:key];
    }
}

- (PlaceHistoryType)getPlaceType
{
    NSUInteger type = [self.type integerValue];
    if (type == 1) {
        return COMMENT;
    } else if (type == 2) {
        return IMAGE;
    } else {
        return CHECKIN;
    }
}

@end
