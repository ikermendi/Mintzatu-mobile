//
//  Utils.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 11/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "NSDate+Utils.h"

@implementation NSDate (Utils)
- (NSDate *)toLocalTime
{
    NSTimeZone *tz = [NSTimeZone localTimeZone];
    NSInteger seconds = [tz secondsFromGMTForDate:self];
    return [NSDate dateWithTimeInterval:seconds sinceDate:self];
}
@end
