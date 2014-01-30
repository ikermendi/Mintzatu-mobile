//
//  SocialEzarpenakViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 30/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SocialEzarpenakViewController.h"

#import "SocialEzarpenakCell.h"
#import "SocialAccountWrapper.h"
#import "TwitterAcountsViewController.h"

@interface SocialEzarpenakViewController ()
{
    SocialAccountWrapper *_socialWrapper;
    
    NSMutableArray *twAcounts;
}
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@end

@implementation SocialEzarpenakViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"SocialEzarpenakViewController";
    _socialWrapper = [[SocialAccountWrapper alloc] init];
    [_socialWrapper registerObserver:self selector:@selector(socialAccountResponse:)];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [_tableView reloadData];

    NSIndexPath *selection = [self.tableView indexPathForSelectedRow];
    if (selection) {
        [self.tableView deselectRowAtIndexPath:selection animated:YES];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    
    [super viewWillDisappear:animated];
}

- (void)viewDidUnload
{
    [_socialWrapper unregisterAllObservers];
}

- (void)dealloc
{
    _socialWrapper = nil;
}

#pragma mark Selectors

- (void)socialAccountResponse:(NSNotification*)notification
{
    NSDictionary *dict = [notification userInfo];
    BOOL granted = [[dict objectForKey:SocialAccountGrantedKey] boolValue];
    BOOL userLogged = [[dict objectForKey:SocialAccountUserLoggedKey] boolValue];
    NSUInteger row;
    if ([[dict objectForKey:SocialAccountAccountTypeKey] isEqualToString:@"Facebook"]) {
        row = 0;
    } else {
        row = 1;
    }
    SocialEzarpenakCell *cell = (SocialEzarpenakCell*) [self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForItem:row inSection:0]];
    if (!granted || !userLogged) {
        
        if(cell.switcher.on){
            //Sale varias veces el aviso -> Comprobar
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Abisua" message:@"Sistemaren ezarpenetan Twitter-a eta Facebook-a konfiguratu" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
            [alert show];
        }
        
        
        cell.switcher.on = NO;
        

    }else{
        //permisos OK -> seleccionar cuenta
        if(row == 1){//solo con Twitter porque permite multicuenta en iOS
            cell.switcher.on = NO;
            [_socialWrapper deactivateTwitter];//Forzamos desactivarlo, y si selecciona cuenta se activa despues
            ACAccountStore *accountStore = [[ACAccountStore alloc] init];
            ACAccountType *twitterType = [accountStore accountTypeWithAccountTypeIdentifier:ACAccountTypeIdentifierTwitter];
            NSArray *accounts = [accountStore accountsWithAccountType:twitterType];
            ACAccount *t;
            //Damos a elegir siempre, con una o con X cuentas.
            twAcounts = [[NSMutableArray alloc]init];
            for(int x=0;x<accounts.count;x++){
                t = [accounts objectAtIndex:x];
                [twAcounts addObject:t];
            }
            
            UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
            TwitterAcountsViewController *twControl = [storyboard instantiateViewControllerWithIdentifier:@"twitterAcountsController"];
            
            twControl.twCell = cell;
            twControl.twArrayAcounts = twAcounts;
            twControl.socialWrapper = _socialWrapper;
            [self.navigationController pushViewController:twControl animated:YES];
            
            //Si queremos seleccionar si solo hay una, y dar a elegir si hay varias
//            if(accounts.count == 1){
//                //Si solo tiene una la seleccionamos automaticamente
//                t = [accounts firstObject];
//                [_socialWrapper setSelectedTwitterAcount:t.identifier];
//            }else{
//                //Si hay varias le damos a elegir
//                //Si no elige ninguna cambiar sl switcher de twitter (row 1) -> cell.switcher.on = NO;
//                twAcounts = [[NSMutableArray alloc]init];
//                for(int x=0;x<accounts.count;x++){
//                    t = [accounts objectAtIndex:x];
//                    [twAcounts addObject:t];
//                }
//                [self performSegueWithIdentifier:@"showTwitterAcounts" sender:cell];
//            }
        }
    }
}

//#pragma mark segue
//
//- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
//{
//    if([segue.identifier isEqualToString:@"showTwitterAcounts"]){
//        //pasar la lista al controller destno
//        //meto el cell en sender para cambiarlo a apagado si cancela
//        TwitterAcountsViewController *twControl = (TwitterAcountsViewController *)segue.destinationViewController;
//        twControl.twCell = sender;
//        twControl.twArrayAcounts = twAcounts;
//        twControl.socialWrapper = _socialWrapper;
//        //twAcounts = nil;
//    }
//}



#pragma mark IBActions

- (void)facebookValueChanged:(id)sender
{
    UISwitch *switcher = (UISwitch*)sender;
    if (switcher.isOn) {
        [_socialWrapper askForFBBasicPermission];
    } else {
        [_socialWrapper deactivateFB];
    }
}

- (void)twitterValueChanged:(id)sender
{
    UISwitch *switcher = (UISwitch*)sender;
    if (switcher.isOn) {
        [_socialWrapper askForTwitterBasicPermission];
    } else {
        [_socialWrapper deactivateTwitter];
    }
}




#pragma mark - TableView

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    SocialEzarpenakCell *cell = (SocialEzarpenakCell*) [tableView dequeueReusableCellWithIdentifier:@"SocialEzarpenakCell"];
    
    if (indexPath.row == 0) {
        cell.type = SimpleCellTypeTop;
        cell.cellTextLabel.text = @"Facebook";
        cell.logo.image = [UIImage imageNamed:@"FacebookLogo"];
        [cell.switcher addTarget:self action:@selector(facebookValueChanged:) forControlEvents:UIControlEventValueChanged];
        
        if ([_socialWrapper isFBActivated])
            cell.switcher.on = YES;
    } else {
        cell.type = SimpleCellTypeBottom;
        cell.cellTextLabel.text = @"Twitter";
        cell.logo.image = [UIImage imageNamed:@"TwitterLogo"];
        [cell.switcher addTarget:self action:@selector(twitterValueChanged:) forControlEvents:UIControlEventValueChanged];
        
        if ([_socialWrapper isTwitterActivated])
            cell.switcher.on = YES;
    }
    
    cell.borderWidth = 1.0f;
    cell.borderColor = [UIColor backgroundBeige];
    
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 56.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10.0f;
}

@end
