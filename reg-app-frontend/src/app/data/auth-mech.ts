import { Observable } from 'rxjs/Observable';

export interface AuthMech {
    id: number;
    name: string;
    displayName: string;
    type: string;
    username: string;
    password: string;
    error: string;
    errorDetail: string;
    federationId: number;
    federation: SamlAuthFederation;
    selectedIdp: SamlAuthIdp;
}

export class SamlAuthFederation {
    id: number;
    name: string;
    entityId: string;
    idpList: SamlAuthIdp[];
    filteredIdpList: Observable<SamlAuthIdp[]>;
}

export class SamlAuthIdp {
    id: number;
    entityId: string;
    displayName: string;
    description: string;
    orgName: string;
}
