//
//  Place.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "Place.h"

#import "PlaceCategory.h"

@implementation Place

- (void)setValue:(id)value forKey:(NSString *)key
{
    if ([key isEqualToString:@"id_lekua"]) {
        self.idLekua = value;
    } else if ([key isEqualToString:@"id_kategoria"]) {
        self.idKategoria = value;
    } else if ([key isEqualToString:@"id_erabiltzaile"]) {
        self.idErabiltzaile = value;
    } else if ([key isEqualToString:@"kategoria"]) {
        self.kategoriak = [[NSMutableArray alloc] init];
        for (NSMutableDictionary *categoryArrayDict in value) {
            PlaceCategory *placeCategory = [[PlaceCategory alloc] initWithDictionary:categoryArrayDict];
            [self.kategoriak addObject:placeCategory];
        }
    } else if ([key isEqualToString:@"helbideaLat"]) {
        self.lat = value;
    } else if ([key isEqualToString:@"helbideaLng"]) {
        self.lng = value;
    } else {
        [super setValue:value forKey:key];
    }
}

@end
