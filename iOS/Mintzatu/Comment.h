//
//  Comment.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

@interface Comment : JSONModel
@property (strong, nonatomic) NSNumber *semeak;
@property (strong, nonatomic) NSString *iruzkina;
@property (strong, nonatomic) NSString *noiz;
@property (strong, nonatomic) NSNumber *idErabiltzaile;
@property (strong, nonatomic) NSNumber *idLeku;
@property (strong, nonatomic) NSNumber *idAita;
@property (strong, nonatomic) NSNumber *idIruzkin;
@property (strong, nonatomic) NSString *userImg;
@property (strong, nonatomic) NSString *izena;
- (CGFloat)commentHeightWithWidth:(CGFloat)width;
@end
