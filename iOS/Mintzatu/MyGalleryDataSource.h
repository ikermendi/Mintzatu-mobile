//
//  MyGalleryDataSource.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 09/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "KTPhotoBrowserDataSource.h"


@interface MyGalleryDataSource : NSObject <KTPhotoBrowserDataSource>
- (id)initWithCompletitonBlock:(void (^)(BOOL success))completition;
@end
