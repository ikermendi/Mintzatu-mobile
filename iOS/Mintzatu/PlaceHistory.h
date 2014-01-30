//
//  PlaceHistory.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

typedef enum {
    COMMENT = 1,
    IMAGE = 2,
    CHECKIN = 3
} PlaceHistoryType;

@interface PlaceHistory : JSONModel
@property (strong, nonatomic) NSNumber *identifier;
@property (strong, nonatomic) NSNumber *idWho;
@property (strong, nonatomic) NSString *who;
@property (strong, nonatomic) NSString *when;
@property (strong, nonatomic) NSString *comment;
@property (strong, nonatomic) NSString *imgName;
@property (strong, nonatomic) NSString *tinyImg;
@property (strong, nonatomic) NSString *normalImg;

- (PlaceHistoryType)getPlaceType;
@end
