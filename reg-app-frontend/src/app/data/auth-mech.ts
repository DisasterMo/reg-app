export class AuthMech {
    id: number;
    name: string;
    displayName: string;
    type: string;
    usernamePassword: UsernamePassword;
    samlLogin: SamlLogin;
}

export class UsernamePassword {
    username: string;
    password: string;
}

export class SamlLogin {
}
