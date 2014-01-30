//
//  FriendRequestCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 02/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"

@class JSFlatButton, FriendRequest, FriendRequestViewController;

@interface FriendRequestCell : SimpleCell
@property (weak, nonatomic) FriendRequestViewController *controller;
@property (nonatomic) FriendRequest *requestFriend;
@property (weak, nonatomic) IBOutlet UIImageView *friendImageView;
@property (weak, nonatomic) IBOutlet JSFlatButton *onartuButton;
@property (weak, nonatomic) IBOutlet JSFlatButton *ukatuButton;
@end
