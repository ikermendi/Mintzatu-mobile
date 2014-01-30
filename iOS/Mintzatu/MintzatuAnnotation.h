//
//  MintzatuAnnotation.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 03/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

@class Place;

@interface MintzatuAnnotation : NSObject <MKAnnotation>
@property (nonatomic) Place *place;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *subtitle;
@property (nonatomic, copy) NSString *imgUrl;
@property (nonatomic, readonly) CLLocationCoordinate2D coordinate;
- (id)initWithTitle:(NSString *)aTitle subtitle:(NSString*)aSubtitle imageURL:(NSString*)imageUrl andCoordinate:(CLLocationCoordinate2D)coord;
- (void)setCoordinate:(CLLocationCoordinate2D)newCoordinate;
@end
