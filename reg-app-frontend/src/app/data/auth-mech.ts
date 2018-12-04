import { Observable } from 'rxjs';

export interface AuthMech {
    id: number;
    createdAt: Date;
    updatedAt: Date;
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
