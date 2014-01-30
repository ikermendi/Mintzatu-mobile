//
//  Activity.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 04/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "Activity.h"

#import <CoreText/CoreText.h>

@implementation Activity

- (void)setValue:(id)value forKey:(NSString *)key
{
    if ([key isEqualToString:@"id"]) {
        self.identifier = value;
    } else if ([key isEqualToString:@"type"]) {
        if ([value isEqualToString:@"checkin"]) {
            self.type = ActivityTypeCheckin;
        } else if ([value isEqualToString:@"comment"]) {
            self.type = ActivityTypeComment;
        } else if ([value isEqualToString:@"image"]) {
            self.type = ActivityTypePhoto;
        }
    } else if ([key isEqualToString:@"normalImg"]) {
        self.imageUrl = value;
    } else if ([key isEqualToString:@"when"]) {
        self.when = [NSDate dateWithTimeIntervalSince1970:[value doubleValue]];
    } else {
        [super setValue:value forKey:key];
    }
}

- (BOOL)hasComment
{
    if (_comment != nil && _comment.length > 0)
        return YES;
    return NO;
}

- (BOOL)hasImage
{
    if (_imageUrl != nil)
        return YES;
    return NO;
}


- (NSString*)getSuffix
{
    NSString *suffix;
    if (_type == ActivityTypeCheckin) {
        suffix = @"Checkin egin du";
    } else if (_type == ActivityTypeComment) {
        suffix = @"Iruzkina egin du";
    } else if (_type == ActivityTypePhoto) {
        suffix = @"Argazki bat gehitu du";
    }
    return suffix;
}

- (BOOL)canExpandWithWidth:(CGFloat)width heigth:(CGFloat)heigth
{
    CGFloat commecntHeigth = [self commentHeightWithWidth:width];
    return commecntHeigth > heigth;
}

- (CGFloat)commentHeightWithWidth:(CGFloat)width
{
    CGSize size = [_comment sizeWithFont:[UIFont fontWithName:@"Helvetica" size:13.0f] constrainedToSize:CGSizeMake(width, 1000)];
    return size.height;
}

#pragma mark Basura de cuando dibujaba con CoreText

- (NSString*)getAllText
{
    return [NSString stringWithFormat:@"%@%@", _who, [self getSuffix]];
}

- (CGRect)textRectWithWidth:(CGFloat)width
{
    NSAttributedString *attributedString = [self getAttributedString];
    CGRect rect = [attributedString boundingRectWithSize:CGSizeMake(width, 10000) options:NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading context:nil];
    return rect;
}


- (NSAttributedString*)getAttributedString
{
	// make a few words bold
	CTFontRef helvetica = CTFontCreateWithName(CFSTR("Helvetica"), 14.0, NULL);
	CTFontRef helveticaBold = CTFontCreateWithName(CFSTR("Helvetica-Bold"), 14.0, NULL);
    
    NSString *suffix = [self getSuffix];
	NSMutableAttributedString *string = [[NSMutableAttributedString alloc] initWithString:[self getAllText]];
    
    [string addAttribute:(id)kCTFontAttributeName
                   value:(__bridge id)helveticaBold
                   range:NSMakeRange(0, [_who length])];
    
    [string addAttribute:(id)kCTFontAttributeName
                   value:(__bridge id)helvetica
                   range:NSMakeRange([_who length], [suffix length])];
    
    
	// add some color
	[string addAttribute:(id)kCTForegroundColorAttributeName
                   value:(id)[UIColor mintzatuBlue].CGColor
                   range:NSMakeRange(0, [_who length])];
    
	[string addAttribute:(id)kCTForegroundColorAttributeName
                   value:(id)[UIColor darkGrayColor].CGColor
                   range:NSMakeRange([_who length], [suffix length])];
    
    CFRelease(helvetica);
	CFRelease(helveticaBold);
    
    return string;
}

@end
