import {OwnerService} from '../../../service/owner.service';
import {Owner} from '../../../dto/owner';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {NgForm, NgModel} from '@angular/forms';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-owner-create',
  templateUrl: './owner-create.component.html',
  styleUrls: ['./owner-create.component.scss']
})
export class OwnerCreateComponent implements OnInit {

  owner: Owner = {
    firstName: '',
    lastName: '',
    email: '',
  };

  constructor(
    private service: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    return 'Create New Owner';
  }

  public get submitButtonText(): string {
    return 'Create';
  }

  get modeIsCreate(): boolean {
    return true;
  }


  private get modeActionFinished(): string {
    return 'created';
  }

  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.owner);
    let observable: Observable<Owner>;
    // eslint-disable-next-line prefer-const
    observable = this.service.create(this.owner);
    observable.subscribe({
      next: data => {
        this.notification.success(`Owner ${this.owner.firstName} successfully ${this.modeActionFinished}.`);
        this.router.navigate(['/owners']);
      },
      error: error => {
        console.error('Error creating owner', error);
        // TODO show an error message to the user. Include and sensibly present the info from the backend!
      }
    });
  }
  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  private loadOwner() {
    const updateID: string | null = this.route.snapshot.paramMap.get('id');
    this.service.getById(Number(updateID)).subscribe(
      (owner: Owner) => {
        this.owner= owner;
      },
      error => {
        console.error('Error loading owner', error);
        // TODO show an error message to the user. Include and sensibly present the info from the backend!
      }
    );
  }
}
