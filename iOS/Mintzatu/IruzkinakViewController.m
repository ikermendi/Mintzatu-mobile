//
//  IruzkinakViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 06/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "IruzkinakViewController.h"

#import "MintzatuAPIClient.h"
#import "UIImageView+WebCache.h"
#import "MintzatuTableView.h"
#import "IruzkinaCell.h"
#import "Comment.h"
#import "REComposeViewController.h"
#import "MBProgressHUD.h"
#import "ZAActivityBar.h"
#import "NSDate+Utils.h"
#import "LagunaProfileViewController.h"
#import <objc/runtime.h>

static char imageViewKey;

@interface IruzkinakViewController () <REComposeViewControllerDelegate>
{
    UIView *_loadingView;
    NSMutableArray *_comments;
    NSUInteger _page;
    NSDateFormatter *_dateFormatter;
    BOOL _finishLoading;
}
@property (weak, nonatomic) IBOutlet MintzatuTableView *tableView;
@end

@implementation IruzkinakViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        _page = 1;
        _finishLoading = NO;
        _dateFormatter = [[NSDateFormatter alloc] init];
        _dateFormatter.timeZone = [NSTimeZone timeZoneWithName:@"Europe/Madrid"];
        [_dateFormatter setDateFormat:@"yyyy/MM/dd HH:mm"];
        _comments = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"IruzkinakViewController";
    ((MintzatuTableView*)(self.tableView)).emptyText = @"Ez dago iruzkinik";
    
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LoadingView" owner:nil options:nil];
    _loadingView = [nib objectAtIndex:0];
    [self.tableView addSubview:_loadingView];

    
    [self activateTableView:NO];
    [self loadData];
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
    [params setValue:_idPlace forKey:@"idPlace"];
    [params setValue:[NSNumber numberWithInt:_page] forKey:@"page"];
    [params setValue:@"20" forKey:@"items"];
    
    [[MintzatuAPIClient sharedClient] postPath:@"get-place-comments" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        NSArray *dictArray = [NSMutableArray arrayWithArray:[responseObject objectForKey:@"comments"]];
        NSMutableArray *indexPaths = [[NSMutableArray alloc] init];
        
        int rowCount = _comments.count;
        int oldRowCount = rowCount;
        
        for (int i = 0; i < 20 && i < dictArray.count; i++) {
            NSDictionary *dict = [dictArray objectAtIndex:i];
            Comment *comment = [[Comment alloc] initWithDictionary:dict];
            [_comments addObject:comment];
            //En la primera carga el array de indexpath no tiene sentido
            //Cuando page>1 se utiliza para animar la tabla
            [indexPaths addObject:[NSIndexPath indexPathForRow:rowCount inSection:0]];
            rowCount++;
        }
        
        if (dictArray.count <= 20)
            _finishLoading = YES;
        
        if (_page == 1) {
            [self.tableView reloadData];
            
            [UIView animateWithDuration:1.0f animations:^{
                _loadingView.alpha = 1.0f;
            } completion:^(BOOL finished) {
                [_loadingView removeFromSuperview];
                _loadingView = nil;
                [self activateTableView:YES];
            }];
            
        } else {
            [self.tableView beginUpdates];
            [self.tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:oldRowCount inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
            if (_finishLoading == NO) {
                [indexPaths addObject:[NSIndexPath indexPathForRow:rowCount inSection:0]];
            }
            [self.tableView insertRowsAtIndexPaths:indexPaths withRowAnimation:UITableViewRowAnimationAutomatic];
            [self.tableView endUpdates];
        }
        _page++;
    } failure:nil];

}

- (void)reloadData
{
    _page = 1;
    _finishLoading = NO;
    [_comments removeAllObjects];
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LoadingView" owner:nil options:nil];
    _loadingView = [nib objectAtIndex:0];
    [self.tableView addSubview:_loadingView];
    [self activateTableView:NO];
    [self loadData];
}

- (void)dealloc
{
    _loadingView = nil;
    _comments = nil;
    _dateFormatter = nil;
}

- (void)avatarTapped:(UITapGestureRecognizer *)gesture
{
    NSIndexPath *indexPath = objc_getAssociatedObject(gesture, &imageViewKey);
    [self performSegueWithIdentifier:@"profile" sender:indexPath];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = (NSIndexPath*)sender;
    Comment *comment = [_comments objectAtIndex:indexPath.row];
    if ([segue.identifier isEqualToString:@"profile"]) {
        LagunaProfileViewController *controller = (LagunaProfileViewController*) segue.destinationViewController;
        controller.profileId = [comment.idErabiltzaile integerValue];
    }
}

#pragma mark - REComposerViewController

- (IBAction)writeComment:(id)sender
{
    REComposeViewController *composeViewController = [[REComposeViewController alloc] init];
    composeViewController.delegate = self;
    composeViewController.hasAttachment = NO;
    if (floor(NSFoundationVersionNumber) < NSFoundationVersionNumber_iOS_6_1) {
        composeViewController.title = @"Iruzkina idatzi";
        [composeViewController.navigationBar setBackgroundImage:nil forBarMetrics:UIBarMetricsDefault];
    }
    [composeViewController presentFromRootViewController];
}

- (void)composeViewController:(REComposeViewController *)composeViewController didFinishWithResult:(REComposeResult)result
{
    if (result == REComposeResultPosted) {
        
        NSString *comment = composeViewController.text;
        if (comment.length == 0) {
            return;
        } else if (comment.length < 20) {
            [ZAActivityBar setLocationTabBar];
            [ZAActivityBar showErrorWithStatus:@"Iruzkina minimo 20 karaktere behar ditu" duration:2.0f];
            return;
        }
        
        NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
        [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
        [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
        [params setValue:_idPlace forKey:@"idPlace"];
        [params setValue:composeViewController.text forKey:@"comment"];
        
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = @"Iruzkina bildaltzen";
        
        void (^block)() = ^() {
            [hud hide:YES];
            [self reloadData];
        };
        
        [[MintzatuAPIClient sharedClient] postPath:@"comment-place" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
            block();
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            block();
        }];
    }
    
    [composeViewController dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - TableView

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (_finishLoading) {
        return _comments.count;
    } else {
        return _comments.count + 1;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    IruzkinaCell *cell = [tableView dequeueReusableCellWithIdentifier:@"IruzkinaCell"];
    
    cell.borderWidth = 1.0f;
    cell.borderColor = [UIColor backgroundBeige];
    
    if (indexPath.row == 0) {
        cell.type = SimpleCellTypeTop;
    } else if (indexPath.row == _comments.count) {
        cell.type = SimpleCellTypeBottom;
    } else {
        cell.type = SimpleCellTypeMiddle;
    }
    
    if (indexPath.row == _comments.count) {
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"IruzkinaLoadingCell"];
        return cell;
    }
    
    Comment *comment = [_comments objectAtIndex:indexPath.row];
    
    [cell.avatarImageView setImageWithURL:[NSURL URLWithString:comment.userImg] placeholderImage:[UIImage imageNamed:@"Placeholder"]];
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(avatarTapped:)];
    [cell.avatarImageView addGestureRecognizer:gesture];
    objc_setAssociatedObject(gesture, &imageViewKey, indexPath, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    cell.nameLabel.text = comment.izena;
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:[comment.noiz intValue]];
    cell.timeLabel.text = [_dateFormatter stringFromDate:[date toLocalTime]];
    cell.iruzkinaLabel.text = comment.iruzkina;
    
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (!_finishLoading && indexPath.row == _comments.count) {
        return 80.0f;
    }
    
    Comment *comment = [_comments objectAtIndex:indexPath.row];
    
    CGFloat height = [comment commentHeightWithWidth:276];
    
    return MAX(height + 80.0f, 80.0f);
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (!_finishLoading && indexPath.row == _comments.count-1) {
        [self loadData];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 10.0f;
}

@end
