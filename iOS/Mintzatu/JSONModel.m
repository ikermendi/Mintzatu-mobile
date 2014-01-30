//
//  JSONModel.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 06/05/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

@implementation JSONModel

- (id)initWithDictionary:(NSDictionary*)jsonDictionary
{
    if ((self = [super init])) {
        self = [self init];
        [self setValuesForKeysWithDictionary:jsonDictionary];
    }
    
    return self;
}

- (id)mutableCopyWithZone:(NSZone *)zone
{
    // subclass implementation should do a deep mutable copy
    // this class doesn't have any ivars so this is ok
	JSONModel *newModel = [[JSONModel allocWithZone:zone] init];
	return newModel;
}

-(id) copyWithZone:(NSZone *)zone
{
    // subclass implementation should do a deep mutable copy
    // this class doesn't have any ivars so this is ok
	JSONModel *newModel = [[JSONModel allocWithZone:zone] init];
	return newModel;
}

- (void)setValue:(id)value forUndefinedKey:(NSString *)key
{
    //NSLog(@"Undefined key: %@", key);
}

@end
