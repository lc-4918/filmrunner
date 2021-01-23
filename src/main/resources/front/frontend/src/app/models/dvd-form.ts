export interface DvdForm {

id?: number;
titre:string;
titreVf?:string;
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

themes?:Map<string,string>;
realisateurs: string[];
shortfilms: string[];

}
