//
//  Category.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

@interface PlaceCategory : JSONModel
@property (strong, nonatomic) NSNumber *identifier;
@property (strong, nonatomic) NSString *izena;
@property (strong, nonatomic) NSString *deskribapena;
@property (strong, nonatomic) NSString *imgUrl;
@end
