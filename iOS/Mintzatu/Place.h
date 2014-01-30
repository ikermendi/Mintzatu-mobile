//
//  Place.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "JSONModel.h"

@class PlaceCategory;

@interface Place : JSONModel
@property (strong, nonatomic) NSNumber *distantzia;
@property (strong, nonatomic) NSNumber *checks;
@property (strong, nonatomic) NSNumber *comments;
@property (strong, nonatomic) NSNumber *idLekua;
@property (strong, nonatomic) NSNumber *idKategoria;
@property (strong, nonatomic) NSNumber *idErabiltzaile;
@property (strong, nonatomic) NSString *izena;
@property (strong, nonatomic) NSString *helbidea;
@property (strong, nonatomic) NSNumber *lat;
@property (strong, nonatomic) NSNumber *lng;
@property (strong, nonatomic) NSString *herria;
@property (strong, nonatomic) NSString *deskribapena;
@property (strong, nonatomic) NSString *url;
@property (strong, nonatomic) NSString *noiz;
@property (strong, nonatomic) NSString *irudia;
@property (strong, nonatomic) NSMutableArray *kategoriak;
@end
