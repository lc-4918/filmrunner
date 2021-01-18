import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {takeWhile} from "rxjs/operators";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  form: any = {};
  hasSubscription = false;
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService,private router: Router, private route: ActivatedRoute) {
    this.hasSubscription = true;
  }

  ngOnInit():void {
  }

  onSubmit():void {
    this.authService.register(this.form).pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      data => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['/login'], { relativeTo: this.route }).then();
      },
      err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    );
  }
  ngOnDestroy():void {
    this.hasSubscription = false;
  }
}
