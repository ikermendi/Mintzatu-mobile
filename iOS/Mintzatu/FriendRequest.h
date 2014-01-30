//
//  FriendRequest.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

@interface FriendRequest : JSONModel
@property (strong, nonatomic) NSString *idRel;
@property (strong, nonatomic) NSNumber *noiz;
@property (strong, nonatomic) NSString *who;
@property (strong, nonatomic) NSNumber *userId;
@property (strong, nonatomic) NSString *userIden;
@property (strong, nonatomic) NSString *userImg;
@end
