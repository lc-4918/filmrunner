import {Theme} from "./theme";

export interface DvdForm {

id?: number;
titre:string;
titrevf?:string;
annee?: number;
duree?: number;
description?: string;
imageUrl?: string;

support?: string;
type?: string;
norme?: string;
details?: string;
format?: string;
source?: string;

pays: string[];
subLangs: string[];

themes?: Theme[];
realisateurs: string[];
shortfilms: string[];

}
