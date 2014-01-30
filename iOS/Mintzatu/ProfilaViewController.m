//
//  ProfilaViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 19/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "ProfilaViewController.h"

#import "MintzatuAPIClient.h"
#import "ProfileDataCell.h"
#import "AwardsCell.h"
#import "SimpleCell.h"
#import "AwardButton.h"
#import "UserCheckinViewController.h"
#import "MyGalleryViewController.h"
#import "MKNumberBadgeView.h"
#import "Me.h"
#import "AFNetworking.h"
#import "MBProgressHUD.h"
#import "BadgesViewController.h"
#import "MayorshipsViewController.h"
#import "FriendRequestViewController.h"
#import "GAITrackedViewController.h"
#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

NSString* const FriendRequestNotification = @"FriendRequestNotification";

@interface ProfilaViewController () <UITableViewDataSource, UITableViewDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate, UIActionSheetDelegate>
{
    UIView *_loadingView;
    MKNumberBadgeView *_requestBadge;
}
@end

@implementation ProfilaViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deleteFriendRequest) name:FriendRequestNotification object:nil];
    
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LoadingView" owner:nil options:nil];
    _loadingView = [nib objectAtIndex:0];
    [self.tableView addSubview:_loadingView];
    [self.refreshControl addTarget:self action:@selector(loadData) forControlEvents:UIControlEventValueChanged];
    [self activateTableView:NO];
    [self loadData];
}

-(void) viewDidAppear:(BOOL)animated
{
    id tracker = [GAI sharedInstance].defaultTracker;
    [tracker set:kGAIScreenName value:@"ProfileViewController"];
    [tracker send:[[GAIDictionaryBuilder createAppView]  build]];
}

- (void)setTabBarBadge
{
    if ([_me.friendRequests intValue] == 0) {
        [[[[[self tabBarController] tabBar] items] objectAtIndex:3] setBadgeValue:nil];
    } else {
        [[[[[self tabBarController] tabBar] items] objectAtIndex:3] setBadgeValue:[NSString stringWithFormat:@"%d", [_me.friendRequests intValue]]];
    }
}

- (void)activateTableView:(BOOL)activate
{
    //Quitamos el scroll para que al cargar no se muestren
    self.tableView.scrollEnabled = activate;
    self.tableView.userInteractionEnabled = activate;
}

- (void)loadData
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[MintzatuAPIClient userId] forKey:@"idProfile"];
    [[MintzatuAPIClient sharedClient] postPath:@"profile" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSDictionary *dict = [responseObject objectForKey:@"profile"];
        _me = [[Me alloc] initWithDictionary:dict];
        
        [self.tableView reloadData];
        
        if (_loadingView != nil) {
            [UIView animateWithDuration:1.0f animations:^{
                _loadingView.alpha = 1.0f;
            } completion:^(BOOL finished) {
                [_loadingView removeFromSuperview];
                [self activateTableView:YES];
                [self setTabBarBadge];
            }];
        } else {
            [self activateTableView:YES];
            [self setTabBarBadge];
        }

        [self.refreshControl endRefreshing];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self.refreshControl endRefreshing];
        double delayInSeconds = 5.0;
        dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
        dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
            [self loadData];
        });
    }];
}

- (void)dealloc
{
    _loadingView = nil;
    _requestBadge = nil;
    _me = nil;
}


-(void)badgesClick
{
    [self performSegueWithIdentifier:@"badges" sender:nil];
}

- (void)mayorshipClick
{
    [self performSegueWithIdentifier:@"mayorships" sender:nil];
}

- (void)deleteFriendRequest
{
    if ([_me.friendRequests intValue] > 0) {
        int value = [_me.friendRequests intValue];
        _me.friendRequests = [NSNumber numberWithInt:value - 1];
    }
    
    if ([_me.friendRequests intValue] == 0) {
        [self.tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:0 inSection:2]] withRowAnimation:UITableViewRowAnimationAutomatic];
        [_requestBadge removeFromSuperview];
        _requestBadge = nil;
    } else {
        _requestBadge.value = [_me.friendRequests intValue];
    }
    
    [self setTabBarBadge];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"badges"]) {
        BadgesViewController *controller = (BadgesViewController*) segue.destinationViewController;
        controller.profileId = [[MintzatuAPIClient userId] integerValue];
    } else if ([segue.identifier isEqualToString:@"mayorships"]) {
        MayorshipsViewController *controller = (MayorshipsViewController*) segue.destinationViewController;
        controller.profileId = [[MintzatuAPIClient userId] integerValue];
    } else if ([segue.identifier isEqualToString:@"friendRequest"]) {
        FriendRequestViewController *controller = (FriendRequestViewController*) segue.destinationViewController;
        controller.profileId = [[MintzatuAPIClient userId] integerValue];
    }
}

#pragma mark Profile picture

- (IBAction)selectProfilePicture:(id)sender
{
    UIActionSheet *actionSheet = [[UIActionSheet alloc]
                                  initWithTitle:@"Argazkia aukeratu"
                                  delegate:self
                                  cancelButtonTitle:@"Cancelar"
                                  destructiveButtonTitle:nil
                                  otherButtonTitles:@"Galería", @"Cámara", nil];
    [actionSheet showFromTabBar:[[self tabBarController] tabBar]];
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    [picker dismissViewControllerAnimated:YES completion:nil];
    UIImage *selectedImage = [info objectForKey:UIImagePickerControllerEditedImage];
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Argazkia aldatzen";
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    
    NSData *imageData = UIImageJPEGRepresentation(selectedImage, 0.5);
    NSURLRequest *request = [[MintzatuAPIClient sharedClient] multipartFormRequestWithMethod:@"POST" path:@"profile-picture" parameters:params constructingBodyWithBlock: ^(id <AFMultipartFormData> formData) {
            [formData appendPartWithFileData:imageData name:@"image" fileName:@"image.jpeg" mimeType:@"image/jpeg"];
    }];
    
    AFJSONRequestOperation *operation = [[AFJSONRequestOperation alloc] initWithRequest:request];
    [operation setUploadProgressBlock:^(NSUInteger bytesWritten, long long totalBytesWritten, long long totalBytesExpectedToWrite) {
        NSLog(@"Sent %lld of %lld bytes", totalBytesWritten, totalBytesExpectedToWrite);
    }];
    
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.tableView reloadData];
            [hud hide:YES];
        });
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [hud hide:YES];
        });
    }];
    
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [queue addOperation:operation];
}

- (IBAction)selectImage:(id)sender
{
    UIActionSheet *actionSheet = [[UIActionSheet alloc]
                                  initWithTitle:@"Argazkia aukeratu"
                                  delegate:self
                                  cancelButtonTitle:@"Cancelar"
                                  destructiveButtonTitle:nil
                                  otherButtonTitles:@"Galería", @"Cámara", nil];
    [actionSheet showFromTabBar:[[self tabBarController] tabBar]];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    if (buttonIndex == 0) {
        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    } else if (buttonIndex == 1) {
        if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            picker.sourceType = UIImagePickerControllerSourceTypeCamera;
        } else {
            picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        }
    } else {
        [actionSheet dismissWithClickedButtonIndex:actionSheet.cancelButtonIndex animated:YES];
        return;
    }
    
    picker.delegate = self;
    picker.allowsEditing = YES;
    
    [self presentViewController:picker animated:YES completion:nil];
}

- (void)navigationController:(UINavigationController *)navController willShowViewController:(UIViewController *)picker animated:(BOOL)animated
{
    picker.navigationItem.title = @"";
}


#pragma mark TableView

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 4;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0) {
        return 3;
    } else if (section == 1 || section == 3) {
        return 1;
    } else {
        if ([_me.friendRequests intValue] > 0) {
            return 3;
        } else {
            return 2;
        }
    }
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = indexPath.section;
    NSUInteger row = indexPath.row;
    
    if (section == 0) {
        if (row == 0 || row == 2) {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SeparatorCell"];
            if (cell == nil) {
                NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SeparatorCellView" owner:self options:nil];
                cell = (UITableViewCell *)[nib objectAtIndex:0];
            }
            return cell;
        } else {
            ProfileDataCell *cell = (ProfileDataCell*) [tableView dequeueReusableCellWithIdentifier:@"ProfilaDataCell"];
            cell.izenaLabel.text = _me.username;
            if (_me.lastPlaceName != nil) {
                cell.azkenLekuaTextField.text = [NSString stringWithFormat:@"Azken lekua:\n%@", _me.lastPlaceName];
            } else {
                cell.azkenLekuaTextField.text = @"Oraindik ez du check-in egin";
            }
            
            cell.azkenLekuaTextField.contentInset = UIEdgeInsetsMake(-4,-4,0,0);
            AFImageRequestOperation *requestOperation = [[AFImageRequestOperation alloc] initWithRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:_me.img]]];
            [requestOperation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
                cell.photoImageView.image = responseObject;
            } failure:nil];
            
            [requestOperation start];
            
            return cell;
        }
    } else if (section == 1) {
        AwardsCell *cell = (AwardsCell*) [tableView dequeueReusableCellWithIdentifier:@"AwardsCell"];
        if (cell == nil) {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"AwardsCellView" owner:self options:nil];
            cell = (AwardsCell *)[nib objectAtIndex:0];
        }
        
        cell.badgeButton.numbeLabel.text = [_me.badges stringValue];
        [cell.badgeButton addTarget:self action:@selector(badgesClick) forControlEvents:UIControlEventTouchUpInside];
        cell.mayorshipButton.numbeLabel.text = [_me.mayorships stringValue];
        [cell.mayorshipButton addTarget:self action:@selector(mayorshipClick) forControlEvents:UIControlEventTouchUpInside];
        
        return cell;
    } else if (section == 2) {
        SimpleCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SimpleCell"];
        if (cell == nil) {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SimpleCellView" owner:self options:nil];
            cell = (SimpleCell *)[nib objectAtIndex:0];
        }
        
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
                        
        if ([_me.friendRequests intValue] > 0) {
            if (row == 0) {
                cell.type = SimpleCellTypeTop;
                cell.cellTextLabel.text = @"Eskaerak";
                _requestBadge = [[MKNumberBadgeView alloc] initWithFrame:CGRectMake(70, -5, 40, 40)];
                _requestBadge.value = [_me.friendRequests intValue];
                _requestBadge.fillColor = [UIColor mintzatuBlue];
                _requestBadge.shadow = NO;
                [cell.containerView addSubview:_requestBadge];
            } else if (row == 1) {
                cell.type = SimpleCellTypeMiddle;
                cell.cellTextLabel.text = @"Egindako check-inak";
            } else if (row == 2) {
                cell.type = SimpleCellTypeBottom;
                cell.cellTextLabel.text = @"Irudiak";
            }
        } else {
            if (row == 0) {
                cell.type = SimpleCellTypeTop;
                cell.cellTextLabel.text = @"Egindako check-inak";
            } else if (row == 1) {
                cell.type = SimpleCellTypeBottom;
                cell.cellTextLabel.text = @"Irudiak";
            }
        }
        
        return cell;
    } else {
        SimpleCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SimpleCell"];
        if (cell == nil) {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SimpleCellView" owner:self options:nil];
            cell = (SimpleCell *)[nib objectAtIndex:0];
        }
        
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
        cell.type = SimpleCellTypeSingle;
        cell.cellTextLabel.text = @"Ezarpenak";
        
        return cell;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = indexPath.section;
    NSUInteger row = indexPath.row;
    
    if (section == 0) {
        if (row == 0 || row == 2)
            return 4.0f;
        else
            return 100.0f;
    } else if (section == 1) {
        return 70.0f;
    }
    
    return 56.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section != 0) {
        return 10.0f;
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if (section > 1) {
        return 10.0f;
    }
    return 0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = [UIColor clearColor];
    return view;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = [UIColor clearColor];
    return view;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = indexPath.row;
    NSUInteger section = indexPath.section;
    
    if (section == 2) {
        if ([_me.friendRequests intValue] > 0) {
            if (row == 0) {
                [self performSegueWithIdentifier:@"friendRequest" sender:indexPath];
            } else if (row == 1) {
                [self performSegueWithIdentifier:@"checkin" sender:indexPath];
            }
        } else {
            if (row == 0) {
                [self performSegueWithIdentifier:@"checkin" sender:indexPath];
            } else if (row == 1) {
                MyGalleryViewController *controller = [[MyGalleryViewController alloc] init];
                [self.navigationController pushViewController:controller animated:YES];
            }
        }
    } else if (section == 3) {
        [self performSegueWithIdentifier:@"ezarpenak" sender:nil];
    }
}

@end
