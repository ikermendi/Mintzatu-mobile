//
//  Activity.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 04/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

typedef enum {
    ActivityTypeCheckin,
    ActivityTypeComment,
    ActivityTypePhoto
} ActivityType;

@interface Activity : JSONModel
@property (nonatomic) NSNumber *identifier;
@property (nonatomic) NSString *who;
@property (nonatomic) NSString *whoImg;
@property (nonatomic) NSNumber *idWho;
@property (nonatomic) NSDate *when;
@property (nonatomic) ActivityType type;
@property (nonatomic) NSString *comment;
@property (nonatomic) NSString *imageUrl;
@property (nonatomic) BOOL expand;
- (BOOL)hasComment;
- (BOOL)hasImage;
- (CGFloat)commentHeightWithWidth:(CGFloat)width;
- (NSString*)getSuffix;
- (BOOL)canExpandWithWidth:(CGFloat)width heigth:(CGFloat)heigth;
@end
