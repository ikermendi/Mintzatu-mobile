//
//  Friend.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

@interface Friend : JSONModel
@property (strong, nonatomic) NSNumber *identifier;
@property (strong, nonatomic) NSString *fullname;
@property (strong, nonatomic) NSString *username;
@property (strong, nonatomic) NSString *town;
@property (strong, nonatomic) NSString *desc;
@property (strong, nonatomic) NSString *facebook;
@property (strong, nonatomic) NSString *twitter;
@property (strong, nonatomic) NSString *lastPlaceName;
@property (strong, nonatomic) NSNumber *badges;
@property (strong, nonatomic) NSNumber *mayorships;
@property (strong, nonatomic) NSNumber *owner;
@property (strong, nonatomic) NSNumber *friendshipRequester;
@property (strong, nonatomic) NSString *friendshipState;
@property (strong, nonatomic) NSString *img;
@end
