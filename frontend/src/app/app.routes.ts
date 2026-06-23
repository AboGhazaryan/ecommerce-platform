import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Mall } from './components/mall/mall';
import { Cart } from './components/cart/cart';
import { Account } from './components/account/account';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { OrderList } from './components/order-list/order-list';
import { OrderDetail } from './components/order-detail/order-detail';
import { AddProduct } from './components/add-product/add-product';
import { Checkout } from './components/checkout/checkout';
import { MyProducts } from './components/my-products/my-products';
import { PendingProducts } from './components/pending-products/pending-products';
import { AdminPanel } from './components/admin-panel/admin-panel';
import { ProductDetail } from './components/product-detail/product-detail';
import { EditProfile } from './components/edit-profile/edit-profile';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '',                    component: Home },
  { path: 'mall',                component: Mall },
  { path: 'cart',                component: Cart },
  { path: 'account',             component: Account,          canActivate: [authGuard] },
  { path: 'account/edit',        component: EditProfile,      canActivate: [authGuard] },
  { path: 'login',               component: Login },
  { path: 'register',            component: Register },
  { path: 'orders',              component: OrderList,        canActivate: [authGuard] },
  { path: 'orders/:id',          component: OrderDetail,      canActivate: [authGuard] },
  { path: 'checkout',            component: Checkout,         canActivate: [authGuard] },
  { path: 'products/new',        component: AddProduct,       canActivate: [authGuard] },
  { path: 'products/my',         component: MyProducts,       canActivate: [authGuard] },
  { path: 'products/pending',    component: PendingProducts,  canActivate: [authGuard] },
  { path: 'products/:id',        component: ProductDetail },
  { path: 'admin',               component: AdminPanel,       canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
