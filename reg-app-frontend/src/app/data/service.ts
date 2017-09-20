import { Policy } from './policy';

export class Service {
    id: number;
    name: string;
    shortName: string;
    description: string;
    shortDescription: string;
    parentServiceId: number;
    policies: Policy[];
}
