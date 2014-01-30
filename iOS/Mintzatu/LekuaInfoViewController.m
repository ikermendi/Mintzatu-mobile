//
//  LekuaViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaInfoViewController.h"

#import "AppDelegate.h"
#import "Place.h"
#import "Badge.h"
#import "Activity.h"
#import "MintzatuAPIClient.h"
#import "SimpleCell.h"
#import "LekuaInfoGenericCell.h"
#import "LekuaInfoPhotoCell.h"
#import "LekuaInfoDataCell.h"
#import "LekuaGalleryViewController.h"
#import "LagunaProfileViewController.h"
#import "CheckinViewController.h"
#import "UIImageView+Webcache.h"
#import "IruzkinakViewController.h"
#import "LekuaInfoImageView.h"
#import "ZoomImageViewController.h"
#import "NSDate+Utils.h"
#import "BadgeWinView.h"
#import "LoadingBackground.h"

#import "objc/runtime.h"
#import "GAITrackedViewController.h"
#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

#define IMAGE_SIZE_WIDTH 305
#define IMAGE_SIZE_HEIGHT 205
#define MARGIN 10
#define TOP_MARGIN 9
#define CELL_WIDTH 240
#define COMMENT_LABEL_WIDTH 271

static char labelKey;
static char imageViewKey;

@interface LekuaInfoViewController () <UITableViewDataSource, UITableViewDelegate, CheckinViewControllerDelegate>
{
    BOOL _showDetailedDescription;
    NSMutableArray *_activityArray;
    NSMutableArray *_pictures;
    NSUInteger _numberOfSections;
    NSDateFormatter *_dateFormatter;
    NSUInteger _page;
    BOOL _finishLoading;
    BOOL _loadingCellDeleted;
    LoadingBackground *_backView;
}
@end

@implementation LekuaInfoViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        _showDetailedDescription = NO;
        _activityArray = [[NSMutableArray alloc] init];
        _dateFormatter = [[NSDateFormatter alloc] init];
        [_dateFormatter setDateFormat:@"yyyy/MM/dd HH:mm"];
        _page = 1;
        _finishLoading = NO;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    id tracker = [GAI sharedInstance].defaultTracker;
    [tracker set:kGAIScreenName value:@"LekuakInfoViewController"];
    [tracker send:[[GAIDictionaryBuilder createAppView]  build]];
    
    AppDelegate *appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    if ([appDelegate canCheckinCoordinate:CLLocationCoordinate2DMake(_place.lat.doubleValue, _place.lng.doubleValue)]) {
         UIBarButtonItem *checkingButton = [[UIBarButtonItem alloc] initWithTitle:@"Check-in" style:UIBarButtonItemStyleBordered target:self action:@selector(loadCheckingView)];
        self.navigationItem.rightBarButtonItem = checkingButton;
    }
    
    [self.refreshControl addTarget:self action:@selector(reloadData) forControlEvents:UIControlEventValueChanged];
    [self loadData];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"profile"]) {
        NSIndexPath *indexPath = (NSIndexPath*)sender;
        Activity *activity = [_activityArray objectAtIndex:indexPath.row];
        LagunaProfileViewController *controller = (LagunaProfileViewController*) segue.destinationViewController;
        controller.profileId = [activity.idWho intValue];
    } else if ([segue.identifier isEqualToString:@"checkin"]) {
        CheckinViewController *controller = (CheckinViewController*) segue.destinationViewController;
        controller.place = _place;
        controller.delegate = self;
    } else if ([segue.identifier isEqualToString:@"iruzkinak"]) {
        IruzkinakViewController *controller = (IruzkinakViewController*) segue.destinationViewController;
        controller.idPlace = _place.idLekua;
    }
}

- (void)loadCheckingView
{
    [self performSegueWithIdentifier:@"checkin" sender:nil];
}

#pragma mark NetWork

- (void)reloadData
{
    _page = 1;
    _finishLoading = NO;
    [_pictures removeAllObjects];
    [_activityArray removeAllObjects];
    [self loadData];
}

- (void)loadData
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:_place.idLekua forKey:@"idPlace"];
    //[params setValue:@"22" forKey:@"idPlace"];
    [params setValue:[NSNumber numberWithInt:_page] forKey:@"page"];
    [params setValue:@"10" forKey:@"items"];
    
    [[MintzatuAPIClient sharedClient] postPath:@"get-place-activity" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSArray *dictArray = [NSMutableArray arrayWithArray:[responseObject objectForKey:@"activities"]];
        NSMutableArray *indexPaths = [[NSMutableArray alloc] init];
        
        int rowCount = _activityArray.count;
        int oldRowCount = rowCount;
        
        int section = 3;
        if (_pictures == nil) {
            section = 2;
        }
        
        for (int i = 0; i < 10 && i < dictArray.count; i++) {
            NSDictionary *dict = [dictArray objectAtIndex:i];
            Activity *activity = [[Activity alloc] initWithDictionary:dict];
            [_activityArray addObject:activity];
            //En la primera carga el array de indexpath no tiene sentido
            //Cuando page > 1 se utiliza para animar la tabla
            [indexPaths addObject:[NSIndexPath indexPathForRow:rowCount inSection:section]];
            rowCount++;
        }

        if (dictArray.count <= 10)
            _finishLoading = YES;
        
        void (^checkListEmptyBlock)() = ^() {
            if (!_loadingCellDeleted && _activityArray.count == 0) {
                [self.tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:0 inSection:2]] withRowAnimation:UITableViewRowAnimationAutomatic];
                _loadingCellDeleted = YES;
            }
        };
        
        if (_page == 1) {
            NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
            [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
            [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
            //[params setValue:@"22" forKey:@"idPlace"];
            [params setValue:_place.idLekua forKey:@"idPlace"];
            
            [[MintzatuAPIClient sharedClient] postPath:@"get-place-pictures" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
                NSArray *pictures = [responseObject objectForKey:@"pictures"];
                if (![pictures isEqual:[NSNull null]]) {
                    _pictures = [NSMutableArray arrayWithArray:pictures];
                }
                
                if (_activityArray.count > 0) {
                    [self.tableView reloadData];
                } else {
                    checkListEmptyBlock();
                }
                [self.refreshControl endRefreshing];
            } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                checkListEmptyBlock();
            }];
            
        } else {
            [self.tableView beginUpdates];
            [self.tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:oldRowCount inSection:section]] withRowAnimation:UITableViewRowAnimationAutomatic];
            if (_finishLoading == NO) {
                [indexPaths addObject:[NSIndexPath indexPathForRow:rowCount inSection:section]];
            }
            [self.tableView insertRowsAtIndexPaths:indexPaths withRowAnimation:UITableViewRowAnimationAutomatic];
            [self.tableView endUpdates];
        }
        _page++;
    } failure:nil];
}

#pragma mark TableView

- (void)labelTapped:(UITapGestureRecognizer *)gesture
{
    NSIndexPath *indexPath = objc_getAssociatedObject(gesture, &labelKey);
    Activity *activity = [_activityArray objectAtIndex:indexPath.row];
    activity.expand = !activity.expand;
    
    LekuaInfoGenericCell *cell = (LekuaInfoGenericCell*) [self.tableView cellForRowAtIndexPath:indexPath];
    UILabel *commentLabel = (UILabel*) [cell viewWithTag:1];
    UIImageView *imageContainer = (UIImageView*) [cell viewWithTag:2];
    
    if (activity.expand) {
        CGFloat height = [activity commentHeightWithWidth:COMMENT_LABEL_WIDTH];
        CGRect commentLabelFrame = commentLabel.frame;
        commentLabelFrame.size = CGSizeMake(COMMENT_LABEL_WIDTH, height);
        commentLabelFrame.origin = CGPointMake(12, 56);
        commentLabel.frame = commentLabelFrame;
        commentLabel.lineBreakMode = NSLineBreakByWordWrapping;
        
        if ([activity hasImage]) {
            CGRect imageContainerFrame = imageContainer.frame;
            imageContainerFrame.origin.y = commentLabelFrame.origin.y + height + MARGIN;
            imageContainer.frame = imageContainerFrame;
        }
    } else {
        commentLabel.frame = CGRectMake(12, 56, COMMENT_LABEL_WIDTH, 30);
        commentLabel.lineBreakMode = NSLineBreakByTruncatingTail;
        
        if ([activity hasImage]) {
            imageContainer.frame = CGRectMake(7.0f, commentLabel.frame.size.height + MARGIN + 56.0f, IMAGE_SIZE_WIDTH, IMAGE_SIZE_HEIGHT);
        }
    }
    
    [self.tableView beginUpdates];
    [self.tableView endUpdates];
}

- (void)avatarTapped:(UITapGestureRecognizer *)gesture
{
    NSIndexPath *indexPath = objc_getAssociatedObject(gesture, &imageViewKey);
    [self performSegueWithIdentifier:@"profile" sender:indexPath];
}

- (UITableViewCell*)configureActivityCellAtIndexPath:(NSIndexPath*)indexPath tableView:(UITableView*)tableView
{
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LekuaInfoGenericCellView" owner:self options:nil];
    LekuaInfoGenericCell *cell = (LekuaInfoGenericCell *)[nib objectAtIndex:0];
    
    cell.type = SimpleCellTypeSingle;

    //Si es la ultima celda ponemos el cargando
    if (!_finishLoading && indexPath.row == _activityArray.count ) {
        SimpleCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"LekuaInfoLoadingCell"];
        return cell;
    } else {
        cell.borderColor = [UIColor backgroundBeige];
        cell.borderWidth = 1.0f;
        
        Activity *activity = [_activityArray objectAtIndex:indexPath.row];
        cell.activity = activity;
        
        [cell.userImageView setImageWithURL:[NSURL URLWithString:activity.whoImg] placeholderImage:[UIImage imageNamed:@"Placeholder"]];
        UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(avatarTapped:)];
        [cell.userImageView addGestureRecognizer:gesture];
        objc_setAssociatedObject(gesture, &imageViewKey, indexPath, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
        
        cell.whoLabel.text = activity.who;
        cell.timeTypeLabel.text = [NSString stringWithFormat:@"%@ - %@", [_dateFormatter stringFromDate:[activity.when toLocalTime]], [activity getSuffix]];
        
        CGFloat originY = 56.0f;
        if ([activity hasComment]) {
            UILabel *commentLabel = [[UILabel alloc] init];
            
            if (activity.expand) {
                CGFloat height = [activity commentHeightWithWidth:COMMENT_LABEL_WIDTH];
                CGRect commentLabelFrame = commentLabel.frame;
                commentLabelFrame.size = CGSizeMake(COMMENT_LABEL_WIDTH, height);
                commentLabelFrame.origin = CGPointMake(12, 56);
                commentLabel.frame = commentLabelFrame;
                commentLabel.lineBreakMode = NSLineBreakByWordWrapping;
            } else {
                commentLabel.frame = CGRectMake(12, 56, COMMENT_LABEL_WIDTH, 30);
                commentLabel.lineBreakMode = NSLineBreakByTruncatingTail;
            }
            
            commentLabel.tag = 1;
            //commentLabel.backgroundColor = [UIColor backgroundBeige];
            commentLabel.textColor = [UIColor darkGrayColor];
            commentLabel.font = [UIFont fontWithName:@"Helvetica" size:13.0f];
            commentLabel.numberOfLines = 0;
            commentLabel.text = activity.comment;
            
            if ([activity canExpandWithWidth:COMMENT_LABEL_WIDTH heigth:30]) {
                commentLabel.userInteractionEnabled = YES;
                UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(labelTapped:)];
                [commentLabel addGestureRecognizer:gesture];
                objc_setAssociatedObject(gesture, &labelKey, indexPath, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
            }
            
            [cell.containerView addSubview:commentLabel];
            originY += commentLabel.frame.size.height + MARGIN;
        }
        
        if ([activity hasImage]) {
            UIImageView *photoImageView = [[UIImageView alloc] initWithFrame:CGRectMake(7.0f, originY, IMAGE_SIZE_WIDTH, IMAGE_SIZE_HEIGHT)];
            [photoImageView setImageWithURL:[NSURL URLWithString:activity.imageUrl] placeholderImage:[UIImage imageNamed:@"Placeholder"]];
            photoImageView.tag = 2;
            photoImageView.userInteractionEnabled = YES;
            photoImageView.clipsToBounds = YES;
            photoImageView.contentMode = UIViewContentModeScaleAspectFill;
            photoImageView.backgroundColor = [UIColor mintzatuOrange];
            [photoImageView setup];
            photoImageView.layer.cornerRadius = 2.0f;
            //photoImageView.layer.masksToBounds = NO;
            photoImageView.layer.shadowOffset = CGSizeMake(1, 2);
            photoImageView.layer.shadowOpacity = 0.8;
            photoImageView.layer.shadowColor = [UIColor blackColor].CGColor;
            photoImageView.layer.shadowPath = [UIBezierPath bezierPathWithRect:photoImageView.bounds].CGPath;
            [cell addSubview:photoImageView];
        }
    }
    
    return cell;

}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = indexPath.row;
    NSUInteger section = indexPath.section;
    
    if (section == 0) {
        if (row == 0 || row == 2) {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SeparatorCell"];
            if (cell == nil) {
                NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SeparatorCellView" owner:self options:nil];
                cell = (UITableViewCell *)[nib objectAtIndex:0];
            }
            return cell;
        }
        
        LekuaInfoDataCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LekuaInfoDataCell"];
        
        cell.cellText = _place.deskribapena;
        
        if ([_place.deskribapena isEqual:[NSNull null]] || _place.deskribapena.length == 0) {
            _place.deskribapena = @"Ez dago deskribapenik";
        }
        
        cell.lekuaDescription.text = _place.deskribapena;
        
        cell.lekuaName.text = _place.izena;
        cell.lekuaStreet.text = _place.helbidea;
        cell.lekuaStreet.contentInset = UIEdgeInsetsMake(-4,-4,0,0);
        cell.lekuaStreet.textColor = [UIColor whiteColor];
        cell.lekuaStreet.font = [UIFont fontWithName:@"Helvetica-Bold" size:11.0f];
        [cell.lekuaImageView setImageWithURL:[NSURL URLWithString:_place.irudia]];
        return cell;
    } else if (section == 1) {
        SimpleCell *cell = [tableView dequeueReusableCellWithIdentifier:@"IruzkinaCell"];
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
        cell.type = SimpleCellTypeTop;
        cell.accessoryView.backgroundColor = [UIColor backgroundBeige];
        cell.cellTextLabel.text = @"Iruzkinak";
        return cell;
    } else if (section == 2 && _numberOfSections == 4) {
        LekuaInfoPhotoCell *cell = (LekuaInfoPhotoCell*) [tableView dequeueReusableCellWithIdentifier:@"LekuaInfoPhotoCell"];
        cell.containerView.layer.borderColor = [[UIColor backgroundBeige] CGColor];
        cell.containerView.layer.borderWidth = 1.0f;
        cell.accessoryView.backgroundColor = [UIColor backgroundBeige];
        [cell setImagesWithPictures:_pictures];
        return cell;
    } else {
        return [self configureActivityCellAtIndexPath:indexPath tableView:tableView];
    }
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = indexPath.section;
    
    if (section == 0) {
        _showDetailedDescription = !_showDetailedDescription;
        [tableView beginUpdates];
        [tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:0 inSection:0]] withRowAnimation:UITableViewRowAnimationNone];
        [tableView insertRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:0 inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
        [tableView endUpdates];
    } else if (section == 1) {
        [self performSegueWithIdentifier:@"iruzkinak" sender:nil];
    } else if (section == 2 && _numberOfSections == 4) {
        LekuaGalleryViewController *lekuaGalleryController = [[LekuaGalleryViewController alloc] init];
        lekuaGalleryController.placeId = _place.idLekua;
        [self.navigationController pushViewController:lekuaGalleryController animated:YES];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if (_pictures == nil)
        _numberOfSections = 3;
    else
        _numberOfSections = 4;
    
    return _numberOfSections;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0)
        return 3;
    else if (section == 1)
        return 1;
    else if (section == 2 && _numberOfSections == 4)
        return 1;
    else {
        if (_finishLoading) {
            return _activityArray.count;
        } else {
            return _activityArray.count+1;
        }
    }
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = indexPath.row;
    NSUInteger section = indexPath.section;
    
    if (section == 0) {
        if (row == 0 || row == 2)
            return 4.0f;
        
        if ([_place.deskribapena isEqual:[NSNull null]])
            return 110.0f;
        
        //LekuaInfoDataCell
        CGSize textSize = [_place.deskribapena sizeWithFont:[UIFont systemFontOfSize:13.0f] constrainedToSize:CGSizeMake(300, INT_MAX)];
        float height;
        if (_showDetailedDescription && textSize.height > 21.0f) {
            height = 110.0f + textSize.height;
        } else {
            height = 110.0f;
        }
        return height;
    } else if (section == 1) {
        return 56.0f;
    } else if (section == 2 && _numberOfSections == 4) {
        return 88.0f;
    } else {
        float height = 66.0f;
        //Comprobamos que no es la celda de cargando
        if (row < _activityArray.count) {
            Activity *activity = [_activityArray objectAtIndex:row];
            
            if (activity.expand) {
                height += [activity commentHeightWithWidth:COMMENT_LABEL_WIDTH] + MARGIN;
            } else {
                if ([activity hasComment]) {
                    height += 30 + MARGIN;
                }
            }
            
            if ([activity hasImage]) {
                height += MARGIN*2 + IMAGE_SIZE_HEIGHT;
            }
        }
        
        return height;
    }
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ((indexPath.section == 3 && _numberOfSections == 4) || (indexPath.section == 2 && _numberOfSections == 3)) {
        if (!_finishLoading && indexPath.row == _activityArray.count-1) {
            [self loadData];
        }
    }
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = [UIColor clearColor];
    return view;
}


- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section == 0)
        return 0.0f;
    
    return 10.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if (section == _numberOfSections-1)
        return 10.0f;
    return 0;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = [UIColor clearColor];
    return view;
}

#pragma mark Badge

- (void)reloadViewWithBadge:(Badge*)badge
{
    if (badge != nil) {
        CGRect backViewFrame = [[UIScreen mainScreen] bounds];
        _backView = [[LoadingBackground alloc] initWithFrame:backViewFrame alpha:0.8];
        _backView.alpha = 1.0;
        [[[UIApplication sharedApplication] keyWindow] addSubview:_backView];
        
        double delayInSeconds = 0.8;
        dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
        dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"BadgeWinView" owner:nil options:nil];
            BadgeWinView *v = (BadgeWinView*) [nib objectAtIndex:0];
            [v.badgeImage setImageWithURL:[NSURL URLWithString:badge.img]];
            v.badgeName.text = badge.name;
            v.transform = CGAffineTransformMakeScale(0, 0);
            [_backView addSubview:v];
            [UIView animateWithDuration:0.5 delay:0 options:UIViewAnimationOptionCurveEaseOut animations:^{
                v.transform = CGAffineTransformIdentity;
            } completion:^(BOOL finished){
                UITapGestureRecognizer *tapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(deleteBadgeView:)];
                [v addGestureRecognizer:tapRecognizer];
            }];
        });
    }
    
    [self reloadData];
}

- (void)deleteBadgeView:(UITapGestureRecognizer*)recognizer
{
    UIView *v = recognizer.view;
    [UIView animateWithDuration:0.5 delay:0 options:UIViewAnimationOptionCurveEaseOut animations:^{
        _backView.alpha = 0;
        v.transform = CGAffineTransformMakeScale(0, 0);
    } completion:^(BOOL finished){
        [_backView removeFromSuperview];
        _backView = nil;
        [v removeFromSuperview];
    }];
}

@end
