//
//  Me.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

@interface Me : JSONModel
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
@property (strong, nonatomic) NSNumber *friendRequests;
@property (strong, nonatomic) NSNumber *checkins;
@property (strong, nonatomic) NSString *img;
@end
