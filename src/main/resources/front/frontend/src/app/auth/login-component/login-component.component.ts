import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {TokenStorageService} from "../../services/token-storage.service";
import {ActivatedRoute, Router, RouterEvent} from "@angular/router";
import {filter, takeUntil, takeWhile} from "rxjs/operators";

@Component({
  selector: 'app-login-component',
  templateUrl: './login-component.component.html',
  styleUrls: ['./login-component.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  form: any = {};
  isLoggedIn = false;
  hasSubscription = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(private authService: AuthService, private tokenStorage: TokenStorageService, private router: Router, private route: ActivatedRoute) {
    this.hasSubscription = true;
  }

  ngOnInit():void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
    }
  }

  onSubmit():void {
    this.authService.login(this.form).pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      data => {
        this.tokenStorage.saveToken(data.token);
        this.tokenStorage.saveUser(data);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        this.authService.loggedIn.next(true);
        this.router.navigate([''], { relativeTo: this.route }).then();
      },
      err => {
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    );
  }

  register():void{
    this.router.navigate(['/register'], { relativeTo: this.route }).then();
  }
  reloadPage():void {
    window.location.reload();
  }
  ngOnDestroy():void{
    this.hasSubscription = false;
  }
}
