//
//  JSONModel.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 06/05/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JSONModel : NSObject <NSCopying, NSMutableCopying>
- (id)initWithDictionary:(NSDictionary*)jsonDictionary;
@end
